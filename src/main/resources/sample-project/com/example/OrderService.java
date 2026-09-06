package com.example;

/**
 * Bundled sample for /codearena scan — intentional smells (not production code).
 */
public class OrderService {

	public String route(int qty, boolean rush, String tier, boolean vip) {
		String out = "none";
		if (qty < 0) {
			if (rush) {
				if (tier.equals("A")) {
					out = "neg-rush-A";
				} else if (tier.equals("B")) {
					out = "neg-rush-B";
				} else if (tier.equals("C")) {
					out = "neg-rush-C";
				} else if (tier.equals("D")) {
					out = "neg-rush-D";
				} else {
					out = "neg-rush-other";
				}
			} else {
				out = vip ? "neg-vip" : "neg";
			}
		} else if (qty == 0) {
			out = rush ? "zero-rush" : "zero";
		} else if (qty < 10) {
			out = vip ? "small-vip" : "small";
		} else if (qty < 100) {
			out = rush ? "mid-rush" : "mid";
		} else if (qty < 1000) {
			out = "large";
		} else {
			out = vip && rush ? "huge-vip-rush" : "huge";
		}
		return out;
	}

	public void checkout(int a, int b, int c, int d, int e, int f, int g) {
		System.out.println("checkout " + a + b + c + d + e + f + g);
	}

	public void longProcess() {
		int total = 0;
		for (int i = 0; i < 80; i++) {
			total += i;
			System.out.println("step " + i);
			if (i % 2 == 0) {
				System.out.println("even");
			} else {
				System.out.println("odd");
			}
			if (i % 3 == 0) {
				System.out.println("div3");
			}
			if (i % 5 == 0) {
				System.out.println("div5");
			}
			for (int j = 0; j < 8; j++) {
				System.out.println("inner " + j);
				if (j == i % 8) {
					break;
				}
			}
			System.out.println("accum " + total);
			System.out.println("trace-a-" + i);
			System.out.println("trace-b-" + i);
			System.out.println("trace-c-" + i);
			System.out.println("trace-d-" + i);
			System.out.println("trace-e-" + i);
			System.out.println("trace-f-" + i);
			System.out.println("trace-g-" + i);
			System.out.println("trace-h-" + i);
			System.out.println("trace-i-" + i);
			System.out.println("trace-j-" + i);
			System.out.println("trace-k-" + i);
			System.out.println("trace-l-" + i);
			System.out.println("trace-m-" + i);
			System.out.println("trace-n-" + i);
			System.out.println("trace-o-" + i);
			System.out.println("pad-0-" + i);
			System.out.println("pad-1-" + i);
			System.out.println("pad-2-" + i);
			System.out.println("pad-3-" + i);
			System.out.println("pad-4-" + i);
			System.out.println("pad-5-" + i);
			System.out.println("pad-6-" + i);
			System.out.println("pad-7-" + i);
			System.out.println("pad-8-" + i);
			System.out.println("pad-9-" + i);
			System.out.println("pad-10-" + i);
			System.out.println("pad-11-" + i);
			System.out.println("pad-12-" + i);
			System.out.println("pad-13-" + i);
			System.out.println("pad-14-" + i);
			System.out.println("pad-15-" + i);
			System.out.println("pad-16-" + i);
			System.out.println("pad-17-" + i);
			System.out.println("pad-18-" + i);
			System.out.println("pad-19-" + i);
		}
		System.out.println("done " + total);
	}

	public void tallyOrders() {
		int total = 0;
		for (int i = 1; i <= 100; i++) {
			total += i;
			if (total > 1000) {
				System.out.println("Exceeded threshold");
			}
		}
		System.out.println("Final sum: " + total);
	}
}
