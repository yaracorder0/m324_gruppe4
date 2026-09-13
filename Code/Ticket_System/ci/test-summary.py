"""
Schreibt eine Tabelle mit den Testresultaten pro Teststufe in die GitHub-Job-Summary
(bzw. auf die Konsole, wenn GITHUB_STEP_SUMMARY nicht gesetzt ist).

Aufruf:  python3 ci/test-summary.py "<Titel>" "<Teststufe>=<Glob>" ...
Beispiel: python3 ci/test-summary.py "employee-service" \
            "Unit-Tests=employee-service/target/surefire-reports/TEST-*.xml" \
            "Integrationstests=employee-service/target/failsafe-reports/TEST-*.xml"
"""
import glob
import os
import sys
import xml.etree.ElementTree as ET


def count(pattern):
    totals = {"tests": 0, "failures": 0, "errors": 0, "skipped": 0}
    files = glob.glob(pattern)
    for path in files:
        root = ET.parse(path).getroot()
        # JUnit-XML: Wurzel ist entweder <testsuite> (Surefire) oder <testsuites> (ijhttp)
        suites = [root] if root.tag == "testsuite" else root.findall("testsuite")
        for suite in suites:
            for key in totals:
                totals[key] += int(suite.get(key, 0))
    return totals, len(files)


def main():
    title, stages = sys.argv[1], sys.argv[2:]
    lines = [
        f"### Testresultate: {title}",
        "",
        "| Teststufe | Tests | Fehlgeschlagen | Fehler | Uebersprungen | Status |",
        "|---|---|---|---|---|---|",
    ]
    for stage in stages:
        label, pattern = stage.split("=", 1)
        t, files = count(pattern)
        if files == 0:
            status = "nicht ausgefuehrt"
        elif t["failures"] or t["errors"]:
            status = "fehlgeschlagen"
        else:
            status = "erfolgreich"
        lines.append(f"| {label} | {t['tests']} | {t['failures']} | {t['errors']} | {t['skipped']} | {status} |")

    output = "\n".join(lines) + "\n"
    summary_file = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_file:
        with open(summary_file, "a", encoding="utf-8") as f:
            f.write(output)
    print(output)


if __name__ == "__main__":
    main()
