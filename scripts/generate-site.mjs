import { readFile, writeFile, mkdir, access } from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { fileURLToPath } from "node:url";

const root = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const docsDir = path.join(root, "docs");
const templatePath = path.join(root, "site", "cv.template.html");
const stylesPath = path.join(root, "site", "site.css");
const scriptPath = path.join(root, "site", "site.js");
const checkOnly = process.argv.includes("--check");

const uiText = {
  en: {
    skip: "Skip to main content",
    languageNavigation: "CV language",
    currentLanguage: "Current language"
  },
  de: {
    skip: "Zum Hauptinhalt springen",
    languageNavigation: "Sprache des Lebenslaufs",
    currentLanguage: "Aktuelle Sprache"
  },
  tr: {
    skip: "Ana içeriğe geç",
    languageNavigation: "Özgeçmiş dili",
    currentLanguage: "Geçerli dil"
  }
};

const categoryIcons = {
  android: "●",
  ios: "●",
  backend: "⌘",
  frontend: "⌨",
  devops: "⚙",
  tools: "⚒",
  ai: "✦",
  design: "✎",
  firebase: "◆",
  databases: "▤",
  languages: "A"
};

function invariant(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

function isObject(value) {
  return value !== null && typeof value === "object" && !Array.isArray(value);
}

function nonEmptyString(value, label) {
  invariant(typeof value === "string" && value.trim().length > 0, `${label} must be a non-empty string`);
  return value.trim();
}

function stringArray(value, label, { allowEmpty = false } = {}) {
  invariant(Array.isArray(value), `${label} must be an array`);
  invariant(allowEmpty || value.length > 0, `${label} must not be empty`);
  const seen = new Set();
  value.forEach((item, index) => {
    const text = nonEmptyString(item, `${label}[${index}]`);
    const normalized = text.toLocaleLowerCase();
    invariant(!seen.has(normalized), `${label} contains duplicate value ${JSON.stringify(text)}`);
    seen.add(normalized);
  });
  return value;
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function safeInlineJson(value) {
  return JSON.stringify(value)
    .replaceAll("<", "\\u003c")
    .replaceAll("\u2028", "\\u2028")
    .replaceAll("\u2029", "\\u2029");
}

function indentSource(source, spaces) {
  const indentation = " ".repeat(spaces);
  return source.trimEnd().split("\n").map((line) => line.length > 0 ? indentation + line : "").join("\n");
}

function httpsUrl(value, label) {
  const text = nonEmptyString(value, label);
  let parsed;
  try {
    parsed = new URL(text);
  } catch {
    throw new Error(`${label} must be a valid URL`);
  }
  invariant(parsed.protocol === "https:", `${label} must use HTTPS`);
  return text;
}

function validateEmail(value, label) {
  const text = nonEmptyString(value, label);
  invariant(/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(text), `${label} must be a valid email address`);
  return text;
}

function parseYearMonth(value, label) {
  const text = nonEmptyString(value, label);
  const match = /^(\d{4})-(\d{2})$/.exec(text);
  invariant(match, `${label} must use YYYY-MM format`);
  const year = Number(match[1]);
  const month = Number(match[2]);
  invariant(year >= 1900 && year <= 2200, `${label} year is outside the supported range`);
  invariant(month >= 1 && month <= 12, `${label} month must be between 01 and 12`);
  return { raw: text, year, month, serial: year * 12 + month };
}

function regexEscape(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

function parsePeriod(value, dictionary, label) {
  const text = nonEmptyString(value, label);
  const separators = ["to", "bis", "–", "—", dictionary.timePeriod.to]
    .filter((item, index, all) => typeof item === "string" && item.length > 0 && all.indexOf(item) === index)
    .sort((left, right) => right.length - left.length)
    .map(regexEscape)
    .join("|");
  const nowTokens = ["now", dictionary.timePeriod.now, dictionary.timePeriod.present]
    .filter((item, index, all) => typeof item === "string" && item.length > 0 && all.indexOf(item) === index)
    .sort((left, right) => right.length - left.length)
    .map(regexEscape)
    .join("|");
  const match = new RegExp(`^(\\d{4}-\\d{2})\\s*(?:${separators})\\s*(\\d{4}-\\d{2}|${nowTokens})$`, "iu").exec(text);
  invariant(match, `${label} has an unsupported period format: ${JSON.stringify(text)}`);
  const start = parseYearMonth(match[1], `${label} start`);
  const isCurrent = !/^\d{4}-\d{2}$/.test(match[2]);
  const end = isCurrent ? null : parseYearMonth(match[2], `${label} end`);
  invariant(isCurrent || end.serial >= start.serial, `${label} ends before it starts`);
  return { start, end, isCurrent };
}

async function readJson(filePath) {
  let source;
  try {
    source = await readFile(filePath, "utf8");
  } catch (error) {
    throw new Error(`Unable to read ${path.relative(root, filePath)}: ${error.message}`);
  }
  try {
    return JSON.parse(source);
  } catch (error) {
    throw new Error(`Invalid JSON in ${path.relative(root, filePath)}: ${error.message}`);
  }
}

async function requireFile(filePath, label) {
  try {
    await access(filePath);
  } catch {
    throw new Error(`${label} is missing: ${path.relative(root, filePath)}`);
  }
}

function validateDictionary(dictionary, code) {
  invariant(isObject(dictionary), `langs.json.${code} must be an object`);
  const requiredGroups = {
    sectionTitles: ["summary", "experience", "languages", "techStack"],
    labels: ["linkedin", "github", "tech", "downloadPdf"],
    opportunityStatus: ["actively_looking", "passive", "not_interested"],
    timePeriod: ["year", "years", "month", "months", "present", "now", "to"]
  };
  for (const [group, keys] of Object.entries(requiredGroups)) {
    invariant(isObject(dictionary[group]), `langs.json.${code}.${group} must be an object`);
    keys.forEach((key) => nonEmptyString(dictionary[group][key], `langs.json.${code}.${group}.${key}`));
  }
  invariant(isObject(dictionary.techCategories), `langs.json.${code}.techCategories must be an object`);
  invariant(isObject(dictionary.months), `langs.json.${code}.months must be an object`);
  for (let month = 1; month <= 12; month += 1) {
    const key = String(month).padStart(2, "0");
    nonEmptyString(dictionary.months[key], `langs.json.${code}.months.${key}`);
  }
}

function validateCv(cv, dictionary, code) {
  invariant(isObject(cv), `${code} CV must be an object`);
  ["name", "title", "location", "summary", "careerStart", "openToOpportunities"].forEach((key) => {
    nonEmptyString(cv[key], `${code} CV.${key}`);
  });
  parseYearMonth(cv.careerStart, `${code} CV.careerStart`);
  invariant(Object.hasOwn(dictionary.opportunityStatus, cv.openToOpportunities), `${code} CV.openToOpportunities has no translation`);

  invariant(isObject(cv.contact), `${code} CV.contact must be an object`);
  validateEmail(cv.contact.email, `${code} CV.contact.email`);
  httpsUrl(cv.contact.linkedin, `${code} CV.contact.linkedin`);
  httpsUrl(cv.contact.github, `${code} CV.contact.github`);

  invariant(Array.isArray(cv.experience) && cv.experience.length > 0, `${code} CV.experience must be a non-empty array`);
  cv.experience.forEach((experience, index) => {
    const label = `${code} CV.experience[${index}]`;
    invariant(isObject(experience), `${label} must be an object`);
    ["title", "type", "company", "location", "period"].forEach((key) => nonEmptyString(experience[key], `${label}.${key}`));
    if (experience.companyWebsite !== undefined) {
      httpsUrl(experience.companyWebsite, `${label}.companyWebsite`);
    }
    if (experience.project !== undefined) {
      nonEmptyString(experience.project, `${label}.project`);
    }
    parsePeriod(experience.period, dictionary, `${label}.period`);
    stringArray(experience.stack, `${label}.stack`);
    stringArray(experience.notes, `${label}.notes`);
  });

  invariant(isObject(cv.languages) && Object.keys(cv.languages).length > 0, `${code} CV.languages must be a non-empty object`);
  Object.entries(cv.languages).forEach(([language, level]) => {
    nonEmptyString(language, `${code} CV.languages key`);
    nonEmptyString(level, `${code} CV.languages.${language}`);
  });

  invariant(isObject(cv.techStack) && Object.keys(cv.techStack).length > 0, `${code} CV.techStack must be a non-empty object`);
  Object.entries(cv.techStack).forEach(([category, items]) => {
    invariant(/^[A-Za-z][A-Za-z0-9_-]*$/.test(category), `${code} CV.techStack category is not a safe identifier: ${category}`);
    nonEmptyString(dictionary.techCategories[category], `langs.json.${code}.techCategories.${category}`);
    stringArray(items, `${code} CV.techStack.${category}`);
  });
}

function validateStacks(stacks, code) {
  invariant(isObject(stacks) && Object.keys(stacks).length > 0, `${code} stacks must be a non-empty object`);
  const seen = new Set();
  Object.entries(stacks).forEach(([name, info]) => {
    const normalized = nonEmptyString(name, `${code} stacks key`).toLocaleLowerCase();
    invariant(!seen.has(normalized), `${code} stacks contains a duplicate case-insensitive key: ${name}`);
    seen.add(normalized);
    invariant(isObject(info), `${code} stacks.${name} must be an object`);
    nonEmptyString(info.description, `${code} stacks.${name}.description`);
    nonEmptyString(info.category, `${code} stacks.${name}.category`);
    httpsUrl(info.url, `${code} stacks.${name}.url`);
  });
}

function formatYearMonth(value, dictionary) {
  return `${dictionary.months[String(value.month).padStart(2, "0")]} ${value.year}`;
}

function formatDuration(totalMonths, dictionary) {
  const years = Math.floor(totalMonths / 12);
  const months = totalMonths % 12;
  const parts = [];
  if (years > 0) {
    parts.push(`${years} ${years === 1 ? dictionary.timePeriod.year : dictionary.timePeriod.years}`);
  }
  if (months > 0) {
    parts.push(`${months} ${months === 1 ? dictionary.timePeriod.month : dictionary.timePeriod.months}`);
  }
  if (parts.length === 0) {
    parts.push(`0 ${dictionary.timePeriod.months}`);
  }
  return parts.join(" ");
}

function renderPeriod(rawPeriod, dictionary, label) {
  const period = parsePeriod(rawPeriod, dictionary, label);
  const start = escapeHtml(formatYearMonth(period.start, dictionary));
  if (period.isCurrent) {
    const attributes = [
      ["data-start", period.start.raw],
      ["data-year", dictionary.timePeriod.year],
      ["data-years", dictionary.timePeriod.years],
      ["data-month", dictionary.timePeriod.month],
      ["data-months", dictionary.timePeriod.months]
    ].map(([name, value]) => `${name}="${escapeHtml(value)}"`).join(" ");
    return `${start} – ${escapeHtml(dictionary.timePeriod.present)}<span class="period-duration" ${attributes}></span>`;
  }
  const totalMonths = period.end.serial - period.start.serial + 1;
  return `${start} – ${escapeHtml(formatYearMonth(period.end, dictionary))} (${escapeHtml(formatDuration(totalMonths, dictionary))})`;
}

function capitalizeForLocale(value, code) {
  const text = String(value);
  return text.charAt(0).toLocaleUpperCase(code) + text.slice(1);
}

function contextForPage(code, languageCodes) {
  const prefix = code === "en" ? "" : "../";
  const routeMap = Object.fromEntries(languageCodes.map((target) => [
    target,
    target === code ? "./" : target === "en" ? prefix || "./" : `${prefix}${target}/`
  ]));
  const outputPath = code === "en" ? path.join(docsDir, "index.html") : path.join(docsDir, code, "index.html");
  const canonicalPath = code === "en" ? "/" : `/${code}/`;
  const suffix = code === "en" ? "" : `_${code}`;
  return {
    prefix,
    routeMap,
    outputPath,
    canonicalUrl: `https://cv.lutfitek.in${canonicalPath}`,
    pdfPath: `${prefix}outputs/pdf/cv${suffix}.pdf`
  };
}

function routingScript(code, routeMap, pdfPath, languageCodes) {
  return `  <script>
    (function () {
      "use strict";
      function decode(value) {
        try { return decodeURIComponent(String(value || "").replace(/\\+/g, " ")); }
        catch (error) { return String(value || ""); }
      }
      var currentLanguage = ${safeInlineJson(code)};
      var supportedLanguages = ${safeInlineJson(languageCodes)};
      var routes = ${safeInlineJson(routeMap)};
      var rawQuery = window.location.search.replace(/^\\?/, "");
      var parts = rawQuery ? rawQuery.split("&") : [];
      var requestedLanguage = "";
      var pdfMode = false;
      var kept = [];
      for (var index = 0; index < parts.length; index += 1) {
        var pair = parts[index].split("=");
        var key = decode(pair.shift()).toLowerCase();
        var value = decode(pair.join("=")).toLowerCase();
        if (key === "lang") { requestedLanguage = value; }
        else {
          kept.push(parts[index]);
          if (key === "pdf" || (key === "mode" && value === "pdf")) { pdfMode = true; }
        }
      }
      if (!requestedLanguage && !pdfMode && currentLanguage === "en" && !(window.Android && typeof window.Android.onPdfReady === "function")) {
        requestedLanguage = String(window.navigator.language || window.navigator.userLanguage || "").split("-")[0].toLowerCase();
      }
      if (supportedLanguages.indexOf(requestedLanguage) === -1) { requestedLanguage = ""; }
      if (requestedLanguage && requestedLanguage !== currentLanguage) {
        kept.push("lang=" + encodeURIComponent(requestedLanguage));
        window.location.replace(routes[requestedLanguage] + (kept.length ? "?" + kept.join("&") : "") + window.location.hash);
        return;
      }
      if (pdfMode && !(window.Android && typeof window.Android.onPdfReady === "function")) {
        window.location.replace(${safeInlineJson(pdfPath)});
      }
    }());
  </script>`;
}

function renderLanguageNavigation(code, languageCodes, context, text) {
  const items = languageCodes.map((target) => {
    const current = target === code ? ' aria-current="page"' : "";
    const currentLabel = target === code ? `<span class="visually-hidden"> (${escapeHtml(text.currentLanguage)})</span>` : "";
    const href = `${context.routeMap[target]}?lang=${encodeURIComponent(target)}`;
    return `        <li><a href="${escapeHtml(href)}" lang="${escapeHtml(target)}" hreflang="${escapeHtml(target)}"${current}><img src="${escapeHtml(`${context.prefix}res/icons/${target}.svg`)}" alt="" width="20" height="14">${escapeHtml(target.toLocaleUpperCase(target))}${currentLabel}</a></li>`;
  }).join("\n");
  return `    <nav class="language-nav" aria-label="${escapeHtml(text.languageNavigation)}">
      <ul>
${items}
      </ul>
    </nav>`;
}

function renderTimeline(cv, dictionary, code) {
  const careerStart = parseYearMonth(cv.careerStart, `${code} CV.careerStart`);
  const startYears = cv.experience.map((experience, index) => parsePeriod(experience.period, dictionary, `${code} CV.experience[${index}].period`).start.year);
  const minimum = Math.min(careerStart.year, ...startYears);
  const maximum = Math.max(...startYears);
  const startYearSet = new Set(startYears);
  const years = [];
  for (let year = maximum; year >= minimum; year -= 1) {
    years.push(startYearSet.has(year) ? `          <li><strong>${year}</strong></li>` : `          <li>${year}</li>`);
  }
  return `      <div class="timeline" aria-hidden="true">
        <ul>
${years.join("\n")}
        </ul>
      </div>`;
}

function renderExperience(cv, dictionary, code) {
  return cv.experience.map((experience, index) => {
    const notes = experience.notes.map((note) => `            <li>${escapeHtml(note)}</li>`).join("\n");
    const stack = experience.stack.map(escapeHtml).join(", ");
    const period = renderPeriod(experience.period, dictionary, `${code} CV.experience[${index}].period`);
    return `        <article class="experience-item">
          <h3>${escapeHtml(experience.title)} <span class="experience-period">${period}</span></h3>
          <p class="company">${escapeHtml(experience.company)}</p>
          <p class="job-meta"><em>${escapeHtml(experience.location)} | ${escapeHtml(experience.type)}</em></p>
          <ul class="notes-list">
${notes}
          </ul>
          <p><strong>${escapeHtml(dictionary.labels.tech)}:</strong> ${stack}</p>
        </article>`;
  }).join("\n");
}

function renderLanguages(cv, code) {
  return Object.entries(cv.languages).map(([language, level]) => `        <li>${escapeHtml(capitalizeForLocale(language, code))}: ${escapeHtml(level)}</li>`).join("\n");
}

function stackLookup(stacks) {
  const entries = new Map();
  Object.entries(stacks).forEach(([name, info]) => {
    entries.set(name.toLocaleLowerCase(), info);
  });
  return (name) => stacks[name] || entries.get(name.toLocaleLowerCase());
}

function renderTechStack(cv, dictionary, stacks, code) {
  const findStack = stackLookup(stacks);
  return Object.entries(cv.techStack).map(([category, items]) => {
    const icon = categoryIcons[category.toLocaleLowerCase()] || "•";
    const list = items.map((item) => {
      const info = findStack(item);
      if (!info) {
        return `            <li>${escapeHtml(item)}</li>`;
      }
      return `            <li><a class="tech-link" href="${escapeHtml(info.url)}" target="_blank" rel="noopener noreferrer" title="${escapeHtml(info.description)}">${escapeHtml(item)}</a></li>`;
    }).join("\n");
    return `        <section class="tech-category" aria-labelledby="tech-${escapeHtml(code)}-${escapeHtml(category)}">
          <h3 id="tech-${escapeHtml(code)}-${escapeHtml(category)}"><span class="category-icon" aria-hidden="true">${escapeHtml(icon)}</span>${escapeHtml(dictionary.techCategories[category])}</h3>
          <ul class="tech-list">
${list}
          </ul>
        </section>`;
  }).join("\n");
}

function renderBody(cv, dictionary, stacks, code, languageCodes, context) {
  const text = uiText[code] || uiText.en;
  const languageNavigation = renderLanguageNavigation(code, languageCodes, context, text);
  const timeline = renderTimeline(cv, dictionary, code);
  const experience = renderExperience(cv, dictionary, code);
  const languages = renderLanguages(cv, code);
  const techStack = renderTechStack(cv, dictionary, stacks, code);
  return `  <a class="skip-link" href="#main-content">${escapeHtml(text.skip)}</a>
  <div class="cv-container">
    <header class="cv-header">
${languageNavigation}
      <h1>${escapeHtml(cv.name)}</h1>
      <div class="contact">
        <a href="mailto:${escapeHtml(cv.contact.email)}">${escapeHtml(cv.contact.email)}</a>
        <a href="${escapeHtml(cv.contact.linkedin)}" target="_blank" rel="noopener noreferrer">${escapeHtml(dictionary.labels.linkedin)}</a>
        <a href="${escapeHtml(cv.contact.github)}" target="_blank" rel="noopener noreferrer">${escapeHtml(dictionary.labels.github)}</a>
      </div>
      <div class="openness">${escapeHtml(dictionary.opportunityStatus[cv.openToOpportunities])}</div>
      <a class="download-pdf" id="download-pdf" href="${escapeHtml(context.pdfPath)}" download>${escapeHtml(dictionary.labels.downloadPdf)}</a>
    </header>

    <main id="main-content">
      <section id="summary" aria-labelledby="summary-title">
        <h2 id="summary-title">${escapeHtml(dictionary.sectionTitles.summary)}</h2>
        <p>${escapeHtml(cv.summary)}</p>
      </section>

      <section id="experience" aria-labelledby="experience-title">
        <h2 id="experience-title">${escapeHtml(dictionary.sectionTitles.experience)}</h2>
        <div class="experience-content">
${timeline}
          <div class="experience-list">
${experience}
          </div>
        </div>
      </section>

      <section id="languages" aria-labelledby="languages-title">
        <h2 id="languages-title">${escapeHtml(dictionary.sectionTitles.languages)}</h2>
        <ul class="languages-list">
${languages}
        </ul>
      </section>

      <section id="tech-stack" aria-labelledby="tech-stack-title">
        <h2 id="tech-stack-title">${escapeHtml(dictionary.sectionTitles.techStack)}</h2>
        <div class="tech-stack-categories">
${techStack}
        </div>
      </section>
    </main>
  </div>`;
}

function renderTemplate(template, values) {
  const placeholder = /{{([A-Za-z][A-Za-z0-9]*)}}/g;
  for (const match of template.matchAll(placeholder)) {
    invariant(Object.hasOwn(values, match[1]), `Template contains unresolved placeholder {{${match[1]}}}`);
  }
  const output = template.replace(placeholder, (_token, key) => String(values[key]));
  invariant(!output.includes("</script>\n</script>"), "Generated HTML contains an invalid nested script terminator");
  return output.replaceAll("\r\n", "\n").replace(/\s+$/, "") + "\n";
}

function internalSafetyChecks() {
  invariant(escapeHtml(`<script>"'&`) === "&lt;script&gt;&quot;&#39;&amp;", "HTML escaping self-test failed");
  invariant(renderTemplate("{{first}}|{{second}}", { first: "{{second}}", second: "safe" }) === "{{second}}|safe\n", "Template replacement must not reinterpret data as template syntax");
  let rejectedUnsafeUrl = false;
  try {
    httpsUrl("javascript:alert(1)", "self-test URL");
  } catch {
    rejectedUnsafeUrl = true;
  }
  invariant(rejectedUnsafeUrl, "URL allowlist self-test failed");
}

async function main() {
  internalSafetyChecks();
  const [template, styles, script, allLanguages] = await Promise.all([
    readFile(templatePath, "utf8"),
    readFile(stylesPath, "utf8"),
    readFile(scriptPath, "utf8"),
    readJson(path.join(docsDir, "langs.json"))
  ]);
  invariant(!script.toLocaleLowerCase().includes("</script"), "site/site.js must not contain a closing script tag");
  invariant(isObject(allLanguages), "langs.json must contain an object");
  const languageCodes = Object.keys(allLanguages);
  invariant(languageCodes.includes("en"), "langs.json must define the default en language");
  invariant(languageCodes.length > 0, "langs.json must define at least one language");
  languageCodes.forEach((code) => {
    invariant(/^[a-z]{2,3}$/.test(code), `Unsupported language code ${JSON.stringify(code)}`);
    validateDictionary(allLanguages[code], code);
  });

  const pages = [];
  for (const code of languageCodes) {
    const suffix = code === "en" ? "" : `_${code}`;
    const cvPath = path.join(docsDir, `cv${suffix}.json`);
    const stacksPath = path.join(docsDir, `stacks${suffix}.json`);
    const flagPath = path.join(docsDir, "res", "icons", `${code}.svg`);
    const pdfPath = path.join(docsDir, "outputs", "pdf", `cv${suffix}.pdf`);
    await Promise.all([
      requireFile(flagPath, `${code} flag`),
      requireFile(pdfPath, `${code} PDF`)
    ]);
    const [cv, stacks] = await Promise.all([readJson(cvPath), readJson(stacksPath)]);
    const dictionary = allLanguages[code];
    validateCv(cv, dictionary, code);
    validateStacks(stacks, code);
    const context = contextForPage(code, languageCodes);
    const alternateLinks = [
      ...languageCodes.map((alternate) => {
        const href = alternate === "en" ? "https://cv.lutfitek.in/" : `https://cv.lutfitek.in/${alternate}/`;
        return `  <link rel="alternate" hreflang="${escapeHtml(alternate)}" href="${escapeHtml(href)}">`;
      }),
      '  <link rel="alternate" hreflang="x-default" href="https://cv.lutfitek.in/">'
    ].join("\n");
    const html = renderTemplate(template, {
      lang: escapeHtml(code),
      metaDescription: escapeHtml(cv.summary),
      pageTitle: escapeHtml(`CV - ${cv.name}`),
      canonicalUrl: escapeHtml(context.canonicalUrl),
      alternateLinks,
      styles: indentSource(styles, 4),
      routingScript: routingScript(code, context.routeMap, context.pdfPath, languageCodes),
      pdfPath: escapeHtml(context.pdfPath),
      cvName: escapeHtml(cv.name),
      body: renderBody(cv, dictionary, stacks, code, languageCodes, context),
      script: indentSource(script, 4)
    });
    pages.push({ code, outputPath: context.outputPath, html });
  }

  let stale = false;
  for (const page of pages) {
    const relative = path.relative(root, page.outputPath);
    if (checkOnly) {
      let current = "";
      try {
        current = await readFile(page.outputPath, "utf8");
      } catch {
        // Report the missing generated file through the same actionable message.
      }
      if (current.replaceAll("\r\n", "\n") !== page.html) {
        console.error(`${relative} is stale. Run: npm run build:site`);
        stale = true;
      } else {
        console.log(`Verified ${relative}`);
      }
    } else {
      await mkdir(path.dirname(page.outputPath), { recursive: true });
      await writeFile(page.outputPath, page.html, "utf8");
      console.log(`Generated ${relative}`);
    }
  }
  if (stale) {
    process.exitCode = 1;
  }
}

main().catch((error) => {
  console.error(`Static site generation failed: ${error.message}`);
  process.exitCode = 1;
});
