(function () {
  "use strict";

  function queryParts() {
    var query = window.location.search.replace(/^\?/, "");
    return query ? query.split("&") : [];
  }

  function decode(value) {
    try {
      return decodeURIComponent(String(value || "").replace(/\+/g, " "));
    } catch (error) {
      return String(value || "");
    }
  }

  function hasPdfMode() {
    var parts = queryParts();
    for (var index = 0; index < parts.length; index += 1) {
      var pair = parts[index].split("=");
      var key = decode(pair.shift()).toLowerCase();
      var value = decode(pair.join("=")).toLowerCase();
      if (key === "pdf" || (key === "mode" && value === "pdf")) {
        return true;
      }
    }
    return false;
  }

  function durationText(totalMonths, node) {
    var years = Math.floor(totalMonths / 12);
    var months = totalMonths % 12;
    var parts = [];
    if (years > 0) {
      parts.push(years + " " + node.getAttribute(years === 1 ? "data-year" : "data-years"));
    }
    if (months > 0) {
      parts.push(months + " " + node.getAttribute(months === 1 ? "data-month" : "data-months"));
    }
    if (parts.length === 0) {
      parts.push("0 " + node.getAttribute("data-months"));
    }
    return " (" + parts.join(" ") + ")";
  }

  function renderLiveDurations() {
    var nodes = document.querySelectorAll(".period-duration[data-start]");
    var now = new Date();
    var endYear = now.getFullYear();
    var endMonth = now.getMonth() + 1;
    for (var index = 0; index < nodes.length; index += 1) {
      var node = nodes[index];
      var start = node.getAttribute("data-start").split("-");
      var startYear = Number(start[0]);
      var startMonth = Number(start[1]);
      var totalMonths = (endYear - startYear) * 12 + (endMonth - startMonth) + 1;
      if (isFinite(totalMonths) && totalMonths > 0) {
        node.textContent = durationText(totalMonths, node);
      }
    }
  }

  function androidBridge() {
    if (window.Android && typeof window.Android.onPdfReady === "function") {
      return window.Android;
    }
    return null;
  }

  function sendPdfToAndroid(pdfPath, bridge) {
    var request = new XMLHttpRequest();
    request.open("GET", pdfPath, true);
    request.responseType = "blob";
    request.onload = function () {
      if (request.status < 200 || request.status >= 300) {
        window.location.href = pdfPath;
        return;
      }
      var reader = new FileReader();
      reader.onload = function () {
        if (typeof reader.result === "string") {
          bridge.onPdfReady(reader.result);
        } else {
          window.location.href = pdfPath;
        }
      };
      reader.onerror = function () {
        window.location.href = pdfPath;
      };
      reader.readAsDataURL(request.response);
    };
    request.onerror = function () {
      window.location.href = pdfPath;
    };
    request.send();
  }

  function pad2(value) {
    return value < 10 ? "0" + value : String(value);
  }

  function safeFilenameName(value) {
    var normalized = value;
    if (typeof normalized.normalize === "function") {
      normalized = normalized.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }
    normalized = normalized.replace(/[^A-Za-z0-9_\-\s]/g, "").replace(/\s+/g, "_");
    return normalized || "CV";
  }

  function configureDownload() {
    var link = document.getElementById("download-pdf");
    if (!link) {
      return;
    }
    var body = document.body;
    var now = new Date();
    var filename = [
      "CV",
      safeFilenameName(body.getAttribute("data-cv-name") || "CV"),
      pad2(now.getMonth() + 1),
      now.getFullYear(),
      body.getAttribute("data-page-lang") || "en"
    ].join("_") + ".pdf";
    link.setAttribute("download", filename);
    link.addEventListener("click", function (event) {
      var bridge = androidBridge();
      if (bridge) {
        event.preventDefault();
        sendPdfToAndroid(body.getAttribute("data-pdf-path"), bridge);
      }
    });
  }

  renderLiveDurations();
  configureDownload();

  if (hasPdfMode()) {
    var bridge = androidBridge();
    if (bridge) {
      sendPdfToAndroid(document.body.getAttribute("data-pdf-path"), bridge);
    }
  }
}());
