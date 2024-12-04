export enum EnergyRating {
  A_PLUS_PLUS = "A++",
  A_PLUS = "A+",
  A = "A",
  B = "B",
  C = "C",
  D = "D",
  E = "E",
}

export function fromString(value: string): string {
  switch (value.toUpperCase()) {
    case "A++":
      return "A_PLUS_PLUS";
    case "A+":
      return "A_PLUS";
    case "A":
      return "A";
    case "B":
      return "B";
    case "C":
      return "C";
    case "D":
      return "D";
    case "E":
      return "E";
    default:
      throw new Error(`Unknown rating: ${value}`);
  }
}

export function toString(value: string): string {
  switch (value) {
    case "A_PLUS_PLUS":
      return "A++";
    case "A_PLUS":
      return "A+";
    case "A":
      return "A";
    case "B":
      return "B";
    case "C":
      return "C";
    case "D":
      return "D";
    case "E":
      return "E";
    default:
      throw new Error(`Unknown rating: ${value}`);
  }
}

export const getAllEnergyRatings = (): { value: string; label: string }[] => {
  return Object.values(EnergyRating).map((rating) => ({
    value: fromString(rating),
    label: rating,
  }));
};
