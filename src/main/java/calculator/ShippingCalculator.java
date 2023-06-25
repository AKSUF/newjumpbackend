package calculator;

public class ShippingCalculator {
	public static double calculateCharge(String shippingAddress, double weight, double dimensions) {
		double baseCharge = 5.0; // Base charge for all packages
		double distanceCharge = 0.0; // Charge based on distance to shipping address
		double weightCharge = 0.0; // Charge based on weight of package
		double dimensionsCharge = 0.0; // Charge based on dimensions of package

		// Calculate the distance charge based on the shipping address
		if (shippingAddress.equals("Chittagong")) {
			distanceCharge = 50.0;
		} else if (shippingAddress.equals("Dhaka")) {
			distanceCharge = 30.0;
		} else if (shippingAddress.equals("Cumilla")) {
			distanceCharge = 40.0;
		} else if (shippingAddress.equals("Barisal")) {
			distanceCharge = 40.0;
		} else if (shippingAddress.equals("Jessore")) {
			distanceCharge = 50.0;
		} else if (shippingAddress.equals("Rajshahi")) {
			distanceCharge = 40.0;
		} else if (shippingAddress.equals("Cox's Bazar")) {
			distanceCharge = 45.0;
		} else if (shippingAddress.equals("Khulna")) {
			distanceCharge = 35.0;
		} else if (shippingAddress.equals("Sylhet")) {
			distanceCharge = 35.0;
		} else {
			distanceCharge = 4.0;
		}

		// Calculate the weight charge based on the weight of the package
		if (weight <= 5.0) {
			weightCharge = 1.0;
		} else if (weight <= 10.0) {
			weightCharge = 2.0;
		} else {
			weightCharge = 3.0;
		}

		// Calculate the dimensions charge based on the dimensions of the package
		if (dimensions <= 50.0) {
			dimensionsCharge = 1.0;
		} else if (dimensions <= 100.0) {
			dimensionsCharge = 2.0;
		} else {
			dimensionsCharge = 3.0;
		}

		double calculatedCharge = baseCharge + distanceCharge + weightCharge + dimensionsCharge;

		return calculatedCharge;
	}
}
