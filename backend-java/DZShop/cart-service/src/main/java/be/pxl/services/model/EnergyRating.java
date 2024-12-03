package be.pxl.services.model;

public enum EnergyRating {
    A_PLUS_PLUS, // Best energy efficiency
    A_PLUS,
    A,
    B,
    C,  // Lower ratings
    D,
    E;

    // You can also add custom methods, like converting from a string or getting descriptions
    public static EnergyRating fromString(String value) {
        switch (value.toUpperCase()) {
            case "A++": return A_PLUS_PLUS;
            case "A+": return A_PLUS;
            case "A": return A;
            case "B": return B;
            case "C": return C;
            case "D": return D;
            case "E": return E;
            default: throw new IllegalArgumentException("Unknown rating: " + value);
        }
    }
}
