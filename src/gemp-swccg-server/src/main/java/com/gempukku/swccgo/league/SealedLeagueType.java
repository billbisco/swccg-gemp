package com.gempukku.swccgo.league;


/**
 * Defines the sealed league types.
 */
public enum SealedLeagueType {

    PREMIERE_ANH_SEALED("premiere_anh_sealed", "premiere_anh_sealed", "Premiere - A New Hope Sealed"),
    HOTH_DAGOBAH_CC_SEALED("hoth_dagobah_cc_sealed", "sealed", "Hoth-Dagobah-Cloud City Sealed"),
    JP_SEALED("jp_sealed", "jp_sealed", "Jabba's Palace Sealed"),
    ENDOR_DSII_SEALED("endor_dsII_sealed", "endor_dsII_sealed", "Endor - Death Star II Sealed"),
    REFLECTIONS_SEALED("reflections_sealed", "reflections_sealed", "Reflections Sealed"),
    EPISODE_I_SEALED("episode_i_sealed", "episode_i_sealed", "Episode I Sealed"),
    ALL_OF_THE_JEDI_SEALED("all_of_the_jedi_sealed", "all_of_the_jedi_sealed", "All Of The Jedi Sealed"),
    NOVELTY_SEALED("novelty_sealed","novelty_sealed", "Novelty - Space Sealed"),
    WATTOS_CUBE_WITH_OBJECTIVE_PACKS("wattos_cube_with_objective", "wattos_cube_with_objective", "Watto's Cube with objective packs Cube"),
    WATTOS_CUBE_WITH_FIXED("wattos_cube_with_fixed", "wattos_cube_with_fixed", "Watto's Cube with fixed pack Cube"),
    ;

    public static SealedLeagueType getLeagueType(String sealedCode) {
        for (SealedLeagueType sealedLeagueType : SealedLeagueType.values()) {
            if (sealedLeagueType.getSealedCode().equals(sealedCode))
                return sealedLeagueType;
        }
        return null;
    }

    private String _sealedCode;
    private String _formatCode;
    private String _humanReadable;

    SealedLeagueType(String sealedCode, String formatCode, String humanReadable) {
        _sealedCode = sealedCode;
        _formatCode = formatCode;
        _humanReadable = humanReadable;
    }

    /**
     * Gets the sealed league code.
     * @return the sealed league code
     */
    public String getSealedCode() {
        return _sealedCode;
    }

    /**
     * Gets the format code.
     * @return the format code
     */
    public String getFormatCode() {
        return _formatCode;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
