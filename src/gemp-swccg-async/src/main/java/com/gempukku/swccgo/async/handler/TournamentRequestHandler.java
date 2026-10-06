package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SortAndFilterCards;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.hall.HallException;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import com.gempukku.swccgo.tournament.ConstructedPlayerStanding;
import com.gempukku.swccgo.tournament.PlayerMadeQueue;
import com.gempukku.swccgo.tournament.Tournament;
import com.gempukku.swccgo.tournament.TournamentService;
import org.apache.commons.text.StringEscapeUtils;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class TournamentRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private TournamentService _tournamentService;
    private SwccgoFormatLibrary _formatLibrary;
    private SwccgCardBlueprintLibrary _library;
    private SortAndFilterCards _sortAndFilterCards;
    private HallServer _hallServer;

    public TournamentRequestHandler(Map<Type, Object> context) {
        super(context);

        _tournamentService = extractObject(context, TournamentService.class);
        _formatLibrary = extractObject(context, SwccgoFormatLibrary.class);
        _library = extractObject(context, SwccgCardBlueprintLibrary.class);
        _sortAndFilterCards = new SortAndFilterCards();
        _hallServer = extractObject(context, HallServer.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if ("".equals(uri) && request.method() == HttpMethod.GET) {
            getCurrentTournaments(request, responseWriter);
        } else if (uri.equals("/products") && request.method() == HttpMethod.GET) {
            getTournamentProducts(responseWriter);
        } else if (uri.equals("/create") && request.method() == HttpMethod.POST) {
            createPlayerMadeTournament(request, responseWriter);
        } else if (uri.equals("/history") && request.method() == HttpMethod.GET) {
            getTournamentHistory(request, responseWriter);
        } else if (uri.startsWith("/") && uri.endsWith("/html") && uri.contains("/deck/") && request.method() == HttpMethod.GET) {
            getTournamentDeck(request, uri.substring(1, uri.indexOf("/deck/")), uri.substring(uri.indexOf("/deck/") + 6, uri.lastIndexOf("/html")), responseWriter);
        } else if (uri.startsWith("/") && request.method() == HttpMethod.GET) {
            getTournamentInfo(request, uri.substring(1), responseWriter);
        } else {
            responseWriter.writeError(404);
        }
    }

    private void getTournamentProducts(ResponseWriter responseWriter) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element products = doc.createElement("products");
        for (com.gempukku.swccgo.tournament.TournamentProduct product : com.gempukku.swccgo.tournament.TournamentProduct.list(null)) {
            Element elem = doc.createElement("product");
            elem.setAttribute("code", product.getCode());
            elem.setAttribute("kind", product.getKind());
            elem.setAttribute("name", product.getDisplayName());
            elem.setAttribute("formatCode", product.getFormatCode());
            elem.setAttribute("liveMax", String.valueOf(product.getLiveMaxPlayers()));
            elem.setAttribute("defaultPacks", String.valueOf(product.defaultPackCount()));
            elem.setAttribute("jsonCube", String.valueOf(product.isJsonCube()));
            elem.setAttribute("wattoCube", String.valueOf(product.isWattoCube()));
            StringBuilder choices = new StringBuilder();
            for (Integer choice : product.packChoices()) {
                if (choices.length() > 0)
                    choices.append(",");
                choices.append(choice);
            }
            elem.setAttribute("packChoices", choices.toString());
            products.appendChild(elem);
        }
        doc.appendChild(products);
        responseWriter.writeXmlResponse(doc);
    }

    private void getTournamentInfo(HttpRequest request, String tournamentId, ResponseWriter responseWriter) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Tournament tournament = _tournamentService.getTournamentById(tournamentId);
        if (tournament == null)
            throw new HttpProcessingException(404);

        Element tournamentElem = doc.createElement("tournament");

        tournamentElem.setAttribute("id", tournament.getTournamentId());
        tournamentElem.setAttribute("name", tournament.getTournamentName());
        tournamentElem.setAttribute("format", _formatLibrary.getFormat(tournament.getFormat()).getName());
        tournamentElem.setAttribute("collection", tournament.getCollectionType().getFullName());
        tournamentElem.setAttribute("round", String.valueOf(tournament.getCurrentRound()));
        tournamentElem.setAttribute("stage", tournament.getTournamentStage().getHumanReadable());

        List<PlayerStanding> leagueStandings = tournament.getCurrentStandings();
        for (PlayerStanding standing : leagueStandings) {
            Element standingElem = doc.createElement("tournamentStanding");
            setStandingAttributes(standing, standingElem);
            tournamentElem.appendChild(standingElem);
        }

        doc.appendChild(tournamentElem);

        responseWriter.writeXmlResponse(doc);
    }

    private void setStandingAttributes(PlayerStanding standing, Element standingElem) {
        standingElem.setAttribute("player", standing.getPlayerName());
        standingElem.setAttribute("standing", String.valueOf(standing.getStanding()));
        standingElem.setAttribute("points", String.valueOf(standing.getPoints()));
        standingElem.setAttribute("gamesPlayed", String.valueOf(standing.getGamesPlayed()));
        DecimalFormat format = new DecimalFormat("##0.00%");
        standingElem.setAttribute("opponentWin", format.format(standing.getOpponentWin()));
        if (standing instanceof ConstructedPlayerStanding) {
            ConstructedPlayerStanding constructed = (ConstructedPlayerStanding) standing;
            standingElem.setAttribute("differential", String.valueOf(constructed.getDifferential()));
            standingElem.setAttribute("lostPile", String.valueOf(constructed.getLostPile()));
            standingElem.setAttribute("hand", String.valueOf(constructed.getHandCards()));
        }
    }

    private void createPlayerMadeTournament(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        try {
            String participantId = getFormParameterSafely(postDecoder, "participantId");
            Player resourceOwner = getResourceOwnerSafely(request, participantId);
            String type = getFormParameterSafely(postDecoder, "type");
            String formatCode = getFormParameterSafely(postDecoder, "formatCode");
            String productCode = getFormParameterSafely(postDecoder, "productCode");
            if (productCode == null || productCode.isEmpty())
                productCode = formatCode;
            String draftMode = getFormParameterSafely(postDecoder, "draftMode");
            String pairing = getFormParameterSafely(postDecoder, "pairing");
            if (pairing == null)
                pairing = PlayerMadeQueue.PAIRING_SWISS;
            String titlePrefix = getFormParameterSafely(postDecoder, "titlePrefix");
            String lightDeckName = getFormParameterSafely(postDecoder, "lightDeckName");
            String darkDeckName = getFormParameterSafely(postDecoder, "darkDeckName");
            boolean lightSample = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "lightSampleDeck"));
            boolean darkSample = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "darkSampleDeck"));
            boolean privateEvent = Boolean.parseBoolean(getFormParameterSafely(postDecoder, "privateEvent"));
            int totalGames = parseIntParam(getFormParameterSafely(postDecoder, "totalGames"), 4);
            int maxPlayers = parseIntParam(getFormParameterSafely(postDecoder, "maxPlayers"), 4);
            int readyCheckSeconds = parseIntParam(getFormParameterSafely(postDecoder, "readyCheckSeconds"), 0);
            int packCount = parseIntParam(getFormParameterSafely(postDecoder, "packCount"), 0);

            Player lightLibrarian = lightSample ? getLibrarian() : null;
            Player darkLibrarian = darkSample ? getLibrarian() : null;
            _hallServer.createPlayerMadeQueue(resourceOwner, titlePrefix, formatCode, pairing, totalGames, maxPlayers,
                    readyCheckSeconds, privateEvent, lightDeckName, lightSample, darkDeckName, darkSample,
                    lightLibrarian, darkLibrarian, type, draftMode, productCode, packCount);
            responseWriter.writeXmlResponse(null);
        } catch (HallException e) {
            responseWriter.writeXmlResponse(marshalException(e));
        } finally {
            postDecoder.destroy();
        }
    }

    private static int parseIntParam(String value, int fallback) {
        if (value == null || value.isEmpty())
            return fallback;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private Document marshalException(HallException e) throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element error = doc.createElement("error");
        error.setAttribute("message", e.getMessage());
        doc.appendChild(error);
        return doc;
    }

    private void getTournamentDeck(HttpRequest request, String tournamentId, String playerName, ResponseWriter responseWriter) throws Exception {
        Tournament tournament = _tournamentService.getTournamentById(tournamentId);
        if (tournament == null)
            throw new HttpProcessingException(404);

        if (tournament.getTournamentStage() != Tournament.Stage.FINISHED)
            throw new HttpProcessingException(403);

        SwccgDeck deck = _tournamentService.getPlayerDeck(tournamentId, playerName);
        if (deck == null)
            throw new HttpProcessingException(404);

        StringBuilder result = new StringBuilder();
        result.append("<html><body>");
        result.append("<h1>" + StringEscapeUtils.escapeHtml3(deck.getDeckName()) + "</h1>");
        result.append("<h2>by " + playerName + "</h2>");
        DefaultCardCollection deckCards = new DefaultCardCollection();
        for (String card : deck.getCards())
            deckCards.addItem(_library.getBaseBlueprintId(card), 1);

        result.append("<br/>");
        result.append("<b>Deck:</b><br/>");
        for (CardCollection.Item item : _sortAndFilterCards.process("sort:cardCategory,name", deckCards.getAll().values(), _library, _formatLibrary, null))
            result.append(item.getCount() + "x " + GameUtils.getFullName(_library.getSwccgoCardBlueprint(item.getBlueprintId())) + "<br/>");

        result.append("</body></html>");

        responseWriter.writeHtmlResponse(result.toString());
    }

    private void getTournamentHistory(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();
        Element tournaments = doc.createElement("tournaments");

        for (Tournament tournament : _tournamentService.getOldTournaments(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7))) {
            Element tournamentElem = doc.createElement("tournament");

            tournamentElem.setAttribute("id", tournament.getTournamentId());
            tournamentElem.setAttribute("name", tournament.getTournamentName());
            tournamentElem.setAttribute("format", _formatLibrary.getFormat(tournament.getFormat()).getName());
            tournamentElem.setAttribute("collection", tournament.getCollectionType().getFullName());
            tournamentElem.setAttribute("round", String.valueOf(tournament.getCurrentRound()));
            tournamentElem.setAttribute("stage", tournament.getTournamentStage().getHumanReadable());

            tournaments.appendChild(tournamentElem);
        }

        doc.appendChild(tournaments);

        responseWriter.writeXmlResponse(doc);
    }

    private void getCurrentTournaments(HttpRequest request, ResponseWriter responseWriter) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();
        Element tournaments = doc.createElement("tournaments");

        for (Tournament tournament : _tournamentService.getLiveTournaments()) {
            Element tournamentElem = doc.createElement("tournament");

            tournamentElem.setAttribute("id", tournament.getTournamentId());
            tournamentElem.setAttribute("name", tournament.getTournamentName());
            tournamentElem.setAttribute("format", _formatLibrary.getFormat(tournament.getFormat()).getName());
            tournamentElem.setAttribute("collection", tournament.getCollectionType().getFullName());
            tournamentElem.setAttribute("round", String.valueOf(tournament.getCurrentRound()));
            tournamentElem.setAttribute("stage", tournament.getTournamentStage().getHumanReadable());

            tournaments.appendChild(tournamentElem);
        }

        doc.appendChild(tournaments);

        responseWriter.writeXmlResponse(doc);
    }
}
