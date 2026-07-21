import fs from "node:fs/promises";
import path from "node:path";
import { FileBlob, SpreadsheetFile } from "@oai/artifact-tool";

const inputPath = "C:/Users/Admin/Downloads/Cinema-Technical.xlsx";
const outputDir = "C:/Users/Admin/Downloads/cgv/outputs/excel_without_masterplan";
const outputPath = path.join(outputDir, "Cinema-Technical_without_masterplan.xlsx");

const input = await FileBlob.load(inputPath);
const workbook = await SpreadsheetFile.importXlsx(input);

const sheets = await workbook.inspect({
  kind: "sheet",
  include: "id,name",
  maxChars: 6000,
});
console.log(sheets.ndjson);

const targetName = "3. masterplan";
const target = workbook.worksheets.getItem(targetName);

if (!target) {
  throw new Error(`Không tìm thấy sheet "${targetName}".`);
}

if (typeof target.delete !== "function") {
  const help = workbook.help("worksheet.delete", {
    include: "index,examples,notes",
    maxChars: 4000,
  });
  console.log(help.ndjson);
  throw new Error("Worksheet delete API is unavailable or named differently.");
}

target.delete();

const verifySheets = await workbook.inspect({
  kind: "sheet",
  include: "id,name",
  maxChars: 6000,
});
console.log("AFTER_DELETE");
console.log(verifySheets.ndjson);

await fs.mkdir(outputDir, { recursive: true });
const output = await SpreadsheetFile.exportXlsx(workbook);
await output.save(outputPath);
console.log(`SAVED ${outputPath}`);
