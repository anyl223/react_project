import { subMonths } from "date-fns";

export const clsx = (...classes: (string | false | null | undefined)[]) => {
  return classes.filter((cls): cls is string => !!cls).join(" ");
};

export const isNotBlank = (val: any): val is string =>
  typeof val === "string" && val.trim().length > 0;

export const isBlank = (val: any) => !isNotBlank(val);

export const convertToDatePickerFormat = (date: Date | string) => {
  if (typeof date === "string") {
    return date.split("/").reverse().join("-");
  } else {
    return date.toLocaleDateString("sv");
  }
};

export const convertBase64ToBlob = (base64Image: string) => {
  // Split into two parts
  const parts = base64Image.split(";base64,");
  // Hold the content type
  const imageType = parts[0].split(":")[1];
  // Decode Base64 string
  const decodedData = window.atob(parts[1]);
  // Create UNIT8ARRAY of size same as row data length
  const uInt8Array = new Uint8Array(decodedData.length);
  // Insert all character code into uInt8Array
  for (let i = 0; i < decodedData.length; ++i) {
    uInt8Array[i] = decodedData.charCodeAt(i);
  }
  // Return BLOB image after conversion
  return new Blob([uInt8Array], { type: imageType });
};
