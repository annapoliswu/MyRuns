package com.zw.myruns;

public class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N1e22a43a0(i);
        return p;
    }
    static double N1e22a43a0(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 63.9443) {
            p = WekaClassifier.N5d99f4421(i);
        } else if (((Double) i[0]).doubleValue() > 63.9443) {
            p = WekaClassifier.N614b23776(i);
        }
        return p;
    }
    static double N5d99f4421(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 0;
        } else if (((Double) i[13]).doubleValue() <= 0.667299) {
            p = 0;
        } else if (((Double) i[13]).doubleValue() > 0.667299) {
            p = WekaClassifier.N2e08bbfe2(i);
        }
        return p;
    }
    static double N2e08bbfe2(Object []i) {
        double p = Double.NaN;
        if (i[21] == null) {
            p = 0;
        } else if (((Double) i[21]).doubleValue() <= 0.89504) {
            p = WekaClassifier.N13d617153(i);
        } else if (((Double) i[21]).doubleValue() > 0.89504) {
            p = WekaClassifier.Na77ae8c5(i);
        }
        return p;
    }
    static double N13d617153(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 0;
        } else if (((Double) i[11]).doubleValue() <= 0.789386) {
            p = WekaClassifier.N185e62214(i);
        } else if (((Double) i[11]).doubleValue() > 0.789386) {
            p = 0;
        }
        return p;
    }
    static double N185e62214(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 0;
        } else if (((Double) i[8]).doubleValue() <= 1.171862) {
            p = 0;
        } else if (((Double) i[8]).doubleValue() > 1.171862) {
            p = 2;
        }
        return p;
    }
    static double Na77ae8c5(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 5.886587) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 5.886587) {
            p = 2;
        }
        return p;
    }
    static double N614b23776(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 557.53006) {
            p = WekaClassifier.N4286fef47(i);
        } else if (((Double) i[0]).doubleValue() > 557.53006) {
            p = WekaClassifier.N67c8d9526(i);
        }
        return p;
    }
    static double N4286fef47(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 107.813836) {
            p = WekaClassifier.N29a2c3ef8(i);
        } else if (((Double) i[0]).doubleValue() > 107.813836) {
            p = WekaClassifier.N1697b6dc15(i);
        }
        return p;
    }
    static double N29a2c3ef8(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 12.716392) {
            p = WekaClassifier.N1e961b999(i);
        } else if (((Double) i[2]).doubleValue() > 12.716392) {
            p = WekaClassifier.N6c78f4e912(i);
        }
        return p;
    }
    static double N1e961b999(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = 0;
        } else if (((Double) i[27]).doubleValue() <= 0.148604) {
            p = WekaClassifier.N26fec3a710(i);
        } else if (((Double) i[27]).doubleValue() > 0.148604) {
            p = WekaClassifier.N537af8d211(i);
        }
        return p;
    }
    static double N26fec3a710(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if (((Double) i[9]).doubleValue() <= 0.675624) {
            p = 3;
        } else if (((Double) i[9]).doubleValue() > 0.675624) {
            p = 0;
        }
        return p;
    }
    static double N537af8d211(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 87.394675) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 87.394675) {
            p = 3;
        }
        return p;
    }
    static double N6c78f4e912(Object []i) {
        double p = Double.NaN;
        if (i[24] == null) {
            p = 0;
        } else if (((Double) i[24]).doubleValue() <= 0.225329) {
            p = 0;
        } else if (((Double) i[24]).doubleValue() > 0.225329) {
            p = WekaClassifier.N421c702c13(i);
        }
        return p;
    }
    static double N421c702c13(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 89.245015) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 89.245015) {
            p = WekaClassifier.N12af703314(i);
        }
        return p;
    }
    static double N12af703314(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 93.603908) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 93.603908) {
            p = 2;
        }
        return p;
    }
    static double N1697b6dc15(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 128.019883) {
            p = WekaClassifier.N3d63d75b16(i);
        } else if (((Double) i[1]).doubleValue() > 128.019883) {
            p = WekaClassifier.N2a52187b22(i);
        }
        return p;
    }
    static double N3d63d75b16(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 6.523917) {
            p = WekaClassifier.N1cdf5dcb17(i);
        } else if (((Double) i[64]).doubleValue() > 6.523917) {
            p = 1;
        }
        return p;
    }
    static double N1cdf5dcb17(Object []i) {
        double p = Double.NaN;
        if (i[13] == null) {
            p = 1;
        } else if (((Double) i[13]).doubleValue() <= 4.149797) {
            p = WekaClassifier.N7518988b18(i);
        } else if (((Double) i[13]).doubleValue() > 4.149797) {
            p = 1;
        }
        return p;
    }
    static double N7518988b18(Object []i) {
        double p = Double.NaN;
        if (i[32] == null) {
            p = 1;
        } else if (((Double) i[32]).doubleValue() <= 2.213556) {
            p = WekaClassifier.N5e92f1a219(i);
        } else if (((Double) i[32]).doubleValue() > 2.213556) {
            p = 3;
        }
        return p;
    }
    static double N5e92f1a219(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 12.824467) {
            p = WekaClassifier.N7d682c2420(i);
        } else if (((Double) i[7]).doubleValue() > 12.824467) {
            p = 3;
        }
        return p;
    }
    static double N7d682c2420(Object []i) {
        double p = Double.NaN;
        if (i[9] == null) {
            p = 3;
        } else if (((Double) i[9]).doubleValue() <= 0.539334) {
            p = WekaClassifier.N1bbf174721(i);
        } else if (((Double) i[9]).doubleValue() > 0.539334) {
            p = 1;
        }
        return p;
    }
    static double N1bbf174721(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 9.19027) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() > 9.19027) {
            p = 3;
        }
        return p;
    }
    static double N2a52187b22(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 52.184008) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > 52.184008) {
            p = WekaClassifier.N6384f37223(i);
        }
        return p;
    }
    static double N6384f37223(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 133.570171) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 133.570171) {
            p = WekaClassifier.N5fb1fa2a24(i);
        }
        return p;
    }
    static double N5fb1fa2a24(Object []i) {
        double p = Double.NaN;
        if (i[30] == null) {
            p = 1;
        } else if (((Double) i[30]).doubleValue() <= 4.687843) {
            p = 1;
        } else if (((Double) i[30]).doubleValue() > 4.687843) {
            p = WekaClassifier.N3cd7dbb425(i);
        }
        return p;
    }
    static double N3cd7dbb425(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() <= 100.978128) {
            p = 2;
        } else if (((Double) i[3]).doubleValue() > 100.978128) {
            p = 1;
        }
        return p;
    }
    static double N67c8d9526(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 1423.674322) {
            p = WekaClassifier.N4227f14b27(i);
        } else if (((Double) i[0]).doubleValue() > 1423.674322) {
            p = WekaClassifier.N101f501229(i);
        }
        return p;
    }
    static double N4227f14b27(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 1280.129675) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 1280.129675) {
            p = WekaClassifier.N1297338f28(i);
        }
        return p;
    }
    static double N1297338f28(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 27.741537) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() > 27.741537) {
            p = 2;
        }
        return p;
    }
    static double N101f501229(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 244.703725) {
            p = WekaClassifier.N72ed67a130(i);
        } else if (((Double) i[2]).doubleValue() > 244.703725) {
            p = WekaClassifier.N624f9b7441(i);
        }
        return p;
    }
    static double N72ed67a130(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 1625.048088) {
            p = WekaClassifier.Nf3d7d1e31(i);
        } else if (((Double) i[0]).doubleValue() > 1625.048088) {
            p = WekaClassifier.N5721d16f34(i);
        }
        return p;
    }
    static double Nf3d7d1e31(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 96.74666) {
            p = WekaClassifier.N241d2c3d32(i);
        } else if (((Double) i[2]).doubleValue() > 96.74666) {
            p = 2;
        }
        return p;
    }
    static double N241d2c3d32(Object []i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 3;
        } else if (((Double) i[6]).doubleValue() <= 22.147397) {
            p = 3;
        } else if (((Double) i[6]).doubleValue() > 22.147397) {
            p = WekaClassifier.N4e68676c33(i);
        }
        return p;
    }
    static double N4e68676c33(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 1537.861997) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 1537.861997) {
            p = 3;
        }
        return p;
    }
    static double N5721d16f34(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 2018.921555) {
            p = WekaClassifier.N3390931835(i);
        } else if (((Double) i[0]).doubleValue() > 2018.921555) {
            p = 3;
        }
        return p;
    }
    static double N3390931835(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 3;
        } else if (((Double) i[64]).doubleValue() <= 37.118966) {
            p = 3;
        } else if (((Double) i[64]).doubleValue() > 37.118966) {
            p = WekaClassifier.N7ea13d9336(i);
        }
        return p;
    }
    static double N7ea13d9336(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 3;
        } else if (((Double) i[19]).doubleValue() <= 7.780283) {
            p = WekaClassifier.N73ff762c37(i);
        } else if (((Double) i[19]).doubleValue() > 7.780283) {
            p = 3;
        }
        return p;
    }
    static double N73ff762c37(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 115.599069) {
            p = WekaClassifier.N2a8c6b9538(i);
        } else if (((Double) i[3]).doubleValue() > 115.599069) {
            p = 2;
        }
        return p;
    }
    static double N2a8c6b9538(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 3;
        } else if (((Double) i[3]).doubleValue() <= 55.440637) {
            p = WekaClassifier.N1db3358f39(i);
        } else if (((Double) i[3]).doubleValue() > 55.440637) {
            p = 3;
        }
        return p;
    }
    static double N1db3358f39(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 3;
        } else if (((Double) i[11]).doubleValue() <= 4.120351) {
            p = 3;
        } else if (((Double) i[11]).doubleValue() > 4.120351) {
            p = WekaClassifier.N6a09de0f40(i);
        }
        return p;
    }
    static double N6a09de0f40(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 2;
        } else if (((Double) i[10]).doubleValue() <= 8.955132) {
            p = 2;
        } else if (((Double) i[10]).doubleValue() > 8.955132) {
            p = 3;
        }
        return p;
    }
    static double N624f9b7441(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 2023.212407) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 2023.212407) {
            p = 3;
        }
        return p;
    }
}
