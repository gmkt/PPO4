package com.itmo.ktelnoy.stockexchange.util;

import java.util.AbstractMap;
import java.util.Map;

public class ProofUpdater {

    public static final String EMPTY_PROOF = "";

    public static String addProofUpdate(String proofA, String proofB) {
        return ""; // A + B
    }

    public static Map.Entry<String, String> subtractProofUpdate(String proofA, Integer amount) {
        return new AbstractMap.SimpleImmutableEntry<>("", ""); // A -> amount + B
    }

}
