package com.gempukku.swccgo.tournament;

import java.util.List;

public final class ConstructedFinishTable {
    private ConstructedFinishTable() {
    }

    public static String sabaccCell(ConstructedPlayerStanding player, List<ConstructedPlayerStanding> all) {
        int group = 0;
        long bestRandom = Long.MIN_VALUE;
        for (ConstructedPlayerStanding other : all) {
            if (tiedOnPrimary(player, other)) {
                group++;
                if (other.getRandomTiebreak() > bestRandom)
                    bestRandom = other.getRandomTiebreak();
            }
        }
        if (group <= 1)
            return "Blank";
        return player.getRandomTiebreak() == bestRandom ? "Won" : "Lost";
    }

    public static boolean tiedOnPrimary(ConstructedPlayerStanding a, ConstructedPlayerStanding b) {
        return a.getPoints() == b.getPoints()
                && a.getDifferential() == b.getDifferential()
                && a.getLostPile() == b.getLostPile()
                && a.getHandCards() == b.getHandCards();
    }

    public static String html(String tournamentName, List<ConstructedPlayerStanding> standings) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tournament ").append(escape(tournamentName)).append(" is finished.<br><br>");
        sb.append("Final Standings:<br>");
        sb.append("<table class='tournament-finish'>");
        sb.append("<tr><th>Rank</th><th>Player</th><th>VP</th><th>Differential</th><th>Lost Pile</th><th>Hand</th><th>Sabacc</th></tr>");
        for (ConstructedPlayerStanding row : standings) {
            sb.append("<tr>");
            sb.append("<td>").append(row.getStanding()).append("</td>");
            sb.append("<td>").append(escape(row.getPlayerName())).append("</td>");
            sb.append("<td>").append(row.getPoints()).append("</td>");
            sb.append("<td>").append(row.getDifferential()).append("</td>");
            sb.append("<td>").append(row.getLostPile()).append("</td>");
            sb.append("<td>").append(row.getHandCards()).append("</td>");
            sb.append("<td>").append(sabaccCell(row, standings)).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private static String escape(String value) {
        if (value == null)
            return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
