import { execFileSync } from "node:child_process";
import { copyFileSync, existsSync, mkdirSync, mkdtempSync, readdirSync, readFileSync, rmSync, statSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import path from "node:path";
import process from "node:process";
import { fileURLToPath } from "node:url";

const root = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const docsDir = path.join(root, "docs");
const outputDir = path.join(docsDir, "outputs");
const pdfDir = path.join(outputDir, "pdf");
const validateOnly = process.argv.includes("--validate-only");

function invariant(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

function nonEmptyString(value, label) {
  invariant(typeof value === "string" && value.trim().length > 0, `${label} must be a non-empty string`);
  return value.trim();
}

function readJson(filePath) {
  let source;
  try {
    source = readFileSync(filePath, "utf8");
  } catch (error) {
    throw new Error(`Unable to read ${path.relative(root, filePath)}: ${error.message}`);
  }
  try {
    return JSON.parse(source);
  } catch (error) {
    throw new Error(`Invalid JSON in ${path.relative(root, filePath)}: ${error.message}`);
  }
}

function capitalizeForLocale(value, code) {
  const text = String(value);
  return text.charAt(0).toLocaleUpperCase(code) + text.slice(1);
}

function markdownText(value) {
  return String(value).replace(/([\\`*_[\]{}()<>#+.!|>~=\-$^:])/g, "\\$1");
}

function markdownList(values) {
  return values.map(markdownText).join(", ");
}

function validateHttpsUrl(value, label) {
  const text = nonEmptyString(value, label);
  let parsed;
  try {
    parsed = new URL(text);
  } catch {
    throw new Error(`${label} must be a valid URL`);
  }
  invariant(parsed.protocol === "https:", `${label} must use HTTPS`);
  return parsed.href;
}

function validateDictionary(dictionary, code) {
  invariant(dictionary && typeof dictionary === "object" && !Array.isArray(dictionary), `langs.json.${code} must be an object`);
  invariant(dictionary.sectionTitles && dictionary.labels, `langs.json.${code} is missing required groups`);
  ["location", "summary", "experience", "techStack", "languages"].forEach((key) => {
    nonEmptyString(dictionary.sectionTitles[key], `langs.json.${code}.sectionTitles.${key}`);
  });
  ["email", "linkedin", "github"].forEach((key) => {
    nonEmptyString(dictionary.labels[key], `langs.json.${code}.labels.${key}`);
  });
}

function validateCv(cv, code) {
  invariant(cv && typeof cv === "object" && !Array.isArray(cv), `${code} CV must be an object`);
  nonEmptyString(cv.name, `${code} CV.name`);
  nonEmptyString(cv.location, `${code} CV.location`);
  if (cv.summary !== undefined) {
    nonEmptyString(cv.summary, `${code} CV.summary`);
  }
  invariant(cv.contact && typeof cv.contact === "object", `${code} CV.contact must be an object`);
  nonEmptyString(cv.contact.email, `${code} CV.contact.email`);
  validateHttpsUrl(cv.contact.linkedin, `${code} CV.contact.linkedin`);
  validateHttpsUrl(cv.contact.github, `${code} CV.contact.github`);
  invariant(Array.isArray(cv.experience), `${code} CV.experience must be an array`);
  invariant(cv.techStack && typeof cv.techStack === "object" && !Array.isArray(cv.techStack), `${code} CV.techStack must be an object`);
  invariant(cv.languages && typeof cv.languages === "object" && !Array.isArray(cv.languages), `${code} CV.languages must be an object`);
}

function generateMarkdown(cv, dictionary, code) {
  const linkedin = validateHttpsUrl(cv.contact.linkedin, `${code} CV.contact.linkedin`);
  const github = validateHttpsUrl(cv.contact.github, `${code} CV.contact.github`);
  let markdown = `# ${markdownText(cv.name)}\n\n`;
  markdown += `**${markdownText(dictionary.sectionTitles.location)}:** ${markdownText(cv.location)}  \n`;
  markdown += `**${markdownText(dictionary.labels.email)}:** ${markdownText(cv.contact.email)}  \n`;
  markdown += `**${markdownText(dictionary.labels.linkedin)}:** [${markdownText(linkedin)}](<${linkedin}>)  \n`;
  markdown += `**${markdownText(dictionary.labels.github)}:** [${markdownText(github)}](<${github}>)  \n\n`;

  if (cv.summary) {
    markdown += `# ${markdownText(dictionary.sectionTitles.summary)}\n\n${markdownText(cv.summary)}\n\n`;
  }

  if (cv.experience.length > 0) {
    markdown += `# ${markdownText(dictionary.sectionTitles.experience)}\n\n`;
    cv.experience.forEach((experience, index) => {
      const label = `${code} CV.experience[${index}]`;
      ["title", "company", "type", "location", "period"].forEach((key) => nonEmptyString(experience[key], `${label}.${key}`));
      invariant(Array.isArray(experience.stack), `${label}.stack must be an array`);
      invariant(Array.isArray(experience.notes), `${label}.notes must be an array`);
      markdown += `### ${markdownText(experience.title)} - ***${markdownText(experience.company)}***\n\n`;
      markdown += `***${markdownText(experience.type)}*** | *${markdownText(experience.location)}* | *${markdownText(experience.period)}*\n\n`;
      if (experience.stack.length > 0) {
        markdown += `**${markdownText(dictionary.sectionTitles.techStack)}:** ${markdownList(experience.stack)}\n\n`;
      }
      experience.notes.forEach((note, noteIndex) => {
        markdown += `- ${markdownText(nonEmptyString(note, `${label}.notes[${noteIndex}]`))}\n`;
      });
      markdown += "\n";
    });
  }

  markdown += `\n# ${markdownText(dictionary.sectionTitles.techStack)}\n\n`;
  Object.entries(cv.techStack).forEach(([category, items]) => {
    invariant(Array.isArray(items), `${code} CV.techStack.${category} must be an array`);
    const categoryTitle = dictionary.techCategories?.[category] || capitalizeForLocale(category, code);
    markdown += `**${markdownText(categoryTitle)}:** ${markdownList(items)}\n\n`;
  });

  markdown += `\n# ${markdownText(dictionary.sectionTitles.languages)}\n\n`;
  Object.entries(cv.languages).forEach(([language, level]) => {
    markdown += `- **${markdownText(capitalizeForLocale(language, code))}:** ${markdownText(nonEmptyString(level, `${code} CV.languages.${language}`))}\n`;
  });
  return markdown;
}

function main() {
  const languages = readJson(path.join(docsDir, "langs.json"));
  invariant(languages && typeof languages === "object" && !Array.isArray(languages), "langs.json must contain an object");
  const codes = Object.keys(languages);
  invariant(codes.includes("en"), "langs.json must define en");
  invariant(codes.length > 0, "langs.json must define at least one language");
  codes.forEach((code) => {
    invariant(/^[a-z]{2,3}$/.test(code), `Unsafe language code ${JSON.stringify(code)}`);
    validateDictionary(languages[code], code);
  });

  const documents = codes.map((code) => {
    const suffix = code === "en" ? "" : `_${code}`;
    const cvPath = path.join(docsDir, `cv${suffix}.json`);
    invariant(existsSync(cvPath), `Missing localized CV: ${path.relative(root, cvPath)}`);
    const cv = readJson(cvPath);
    validateCv(cv, code);
    const markdown = generateMarkdown(cv, languages[code], code);
    return { code, suffix, markdown };
  });

  const expectedPdfNames = new Set(documents.map(({ suffix }) => `cv${suffix}.pdf`));
  if (existsSync(pdfDir)) {
    readdirSync(pdfDir).filter((name) => name.toLocaleLowerCase().endsWith(".pdf")).forEach((name) => {
      invariant(expectedPdfNames.has(name), `Unexpected PDF for a removed or unknown language: docs/outputs/pdf/${name}`);
    });
  }

  if (validateOnly) {
    console.log(`Validated PDF inputs for ${documents.map(({ code }) => code).join(", ")}`);
    return;
  }

  mkdirSync(pdfDir, { recursive: true });
  const workDir = mkdtempSync(path.join(tmpdir(), "resume-pdf-"));
  try {
    const builtPdfs = [];
    for (const { suffix, markdown } of documents) {
      const markdownPath = path.join(workDir, `cv${suffix}.md`);
      const pdfPath = path.join(workDir, `cv${suffix}.pdf`);
      writeFileSync(markdownPath, markdown, "utf8");
      execFileSync("pandoc", [
        markdownPath,
        "-o",
        pdfPath,
        "--pdf-engine=xelatex",
        "--from=markdown-raw_tex-raw_html",
        "-V",
        "geometry:margin=1.5cm"
      ], { cwd: root, env: process.env, stdio: "inherit" });
      invariant(existsSync(pdfPath) && statSync(pdfPath).size > 0, `Pandoc did not produce cv${suffix}.pdf`);
      builtPdfs.push({ pdfPath, destination: path.join(pdfDir, `cv${suffix}.pdf`) });
    }
    for (const { pdfPath, destination } of builtPdfs) {
      copyFileSync(pdfPath, destination);
      console.log(`Created ${path.relative(root, destination)}`);
    }
  } finally {
    rmSync(workDir, { force: true, recursive: true });
  }
}

try {
  main();
} catch (error) {
  console.error(`PDF generation failed: ${error.message}`);
  process.exitCode = 1;
}
