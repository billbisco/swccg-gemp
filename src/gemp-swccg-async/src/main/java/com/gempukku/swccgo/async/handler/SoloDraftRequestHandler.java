package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft2.SoloDraft;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.league.LeagueData;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.league.NewSoloDraftLeagueData;
import org.apache.logging.log4j.Logger;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SoloDraftRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private CollectionsManager _collectionsManager;
    private SoloDraftDefinitions _soloDraftDefinitions;
    private LeagueService _leagueService;
    private SwccgCardBlueprintLibrary _library;
    private HallServer _hallServer;

    public SoloDraftRequestHandler(Map<Type, Object> context) {
        super(context);
        _leagueService = extractObject(context, LeagueService.class);
        _soloDraftDefinitions = extractObject(context, SoloDraftDefinitions.class);
        _collectionsManager = extractObject(context, CollectionsManager.class);
        _library = extractObject(context, SwccgCardBlueprintLibrary.class);
        _hallServer = extractObject(context, HallServer.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        if (uri.startsWith("/") && request.getMethod() == HttpMethod.POST) {
            makePick(request, uri.substring(1), responseWriter);
        } else if (uri.startsWith("/") && request.getMethod() == HttpMethod.GET) {
            getAvailablePicks(request, uri.substring(1), responseWriter);
        } else {
            throw new HttpProcessingException(404);
        }
    }

    private void getAvailablePicks(HttpRequest request, String leagueType, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        DraftSession session = resolveDraftSession(leagueType, resourceOwner, false);
        if (session == null)
            throw new HttpProcessingException(404);

        CardCollection collection = session.collection;
        Iterable<SoloDraft.DraftChoice> availableChoices;
        boolean finished = extraFlag(collection, "finished");
        int stage = extraInt(collection, "stage");
        int stages = extraInt(collection, "stageCount");
        if (!finished) {
            long playerSeed = extraLong(collection, "seed");
            availableChoices = session.soloDraft.getAvailableChoices(playerSeed, stage, collection, null);
        } else {
            availableChoices = Collections.emptyList();
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element availablePicksElem = doc.createElement("availablePicks");

        Element draftStateElem = doc.createElement("state");
        draftStateElem.setAttribute("stage",String.valueOf(stage));
        draftStateElem.setAttribute("stages",String.valueOf(stages));
        availablePicksElem.appendChild(draftStateElem);

        doc.appendChild(availablePicksElem);

        appendAvailablePics(doc, availablePicksElem, availableChoices);

        responseWriter.writeXmlResponse(doc);
    }

    private League findLeagueByType(String leagueType) {
        for (League activeLeague : _leagueService.getActiveLeagues()) {
            if (activeLeague.getType().equals(leagueType))
                return activeLeague;
        }
        return null;
    }

    private void makePick(HttpRequest request, String leagueType, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String selectedChoiceId = getFormParameterSafely(postDecoder, "choiceId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        DraftSession session = resolveDraftSession(leagueType, resourceOwner, true);
        if (session == null)
            throw new HttpProcessingException(404);

        CardCollection collection = session.collection;
        if (extraFlag(collection, "finished"))
            throw new HttpProcessingException(404);

        int stage = extraInt(collection, "stage");
        int stages = extraInt(collection, "stageCount");
        long playerSeed = extraLong(collection, "seed");
        SoloDraft soloDraft = session.soloDraft;
        CollectionType collectionType = session.collectionType;

        Iterable<SoloDraft.DraftChoice> possibleChoices = soloDraft.getAvailableChoices(playerSeed, stage, collection, null);
        SoloDraft.DraftChoice draftChoice = getSelectedDraftChoice(selectedChoiceId, possibleChoices);
        if (draftChoice == null)
            throw new HttpProcessingException(400);

        CardCollection selectedCards = soloDraft.getCardsForChoiceId(selectedChoiceId, playerSeed, stage, collection);

        Map<String, Object> extraInformationChanges = new HashMap<String, Object>();
        boolean hasNextStage = soloDraft.hasNextStage(playerSeed, stage);
        extraInformationChanges.put("stage", stage + 1);
        if (!hasNextStage)
            extraInformationChanges.put("finished", true);

        _collectionsManager.addItemsToPlayerCollection(false, "Draft pick", resourceOwner, collectionType, selectedCards.getAll().values(), extraInformationChanges);
        collection = _collectionsManager.getPlayerCollection(resourceOwner, collectionType.getCode());

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();

        Element pickResultElem = doc.createElement("pickResult");

        Element draftStateElem = doc.createElement("state");
        draftStateElem.setAttribute("stage",String.valueOf(stage + 1));
        draftStateElem.setAttribute("stages",String.valueOf(stages));
        pickResultElem.appendChild(draftStateElem);

        doc.appendChild(pickResultElem);

        for (CardCollection.Item item : selectedCards.getAll().values()) {
            Element pickedCard = doc.createElement("pickedCard");
            String blueprintId = item.getBlueprintId();
            pickedCard.setAttribute("blueprintId", blueprintId);
            pickedCard.setAttribute("count", String.valueOf(item.getCount()));
            SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                if (blueprint.isHorizontal()) {
                    pickedCard.setAttribute("horizontal", "true");
                }
                pickedCard.setAttribute("side", blueprint.getSide().toString().toLowerCase());
            }
            pickResultElem.appendChild(pickedCard);
        }

        if (hasNextStage) {
            Iterable<SoloDraft.DraftChoice> availableChoices = soloDraft.getAvailableChoices(playerSeed, stage + 1, collection, selectedChoiceId);
            appendAvailablePics(doc, pickResultElem, availableChoices);
        }

        responseWriter.writeXmlResponse(doc);
    }

    private void appendAvailablePics(Document doc, Element rootElem, Iterable<SoloDraft.DraftChoice> availablePics) {
        for (SoloDraft.DraftChoice availableChoice : availablePics) {
            String choiceId = availableChoice.getChoiceId();
            String blueprintId = availableChoice.getBlueprintId();
            String choiceUrl = availableChoice.getChoiceUrl();
            String packDesc = availableChoice.getObjPackDescription();
            Element availablePick = doc.createElement("availablePick");
            availablePick.setAttribute("id", choiceId);
            if (blueprintId != null) {
                availablePick.setAttribute("blueprintId", blueprintId);
                SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
                if (blueprint != null) {
                    if (blueprint.isHorizontal()) {
                        availablePick.setAttribute("horizontal", "true");
                    }
                    availablePick.setAttribute("side", blueprint.getSide().toString().toLowerCase());
                }
            }
            if (choiceUrl != null)
                availablePick.setAttribute("url", choiceUrl);
            if (packDesc != null)
                availablePick.setAttribute("desc", packDesc);
            rootElem.appendChild(availablePick);
        }
    }

    private SoloDraft.DraftChoice getSelectedDraftChoice(String choiceId, Iterable<SoloDraft.DraftChoice> availableChoices) {
        for (SoloDraft.DraftChoice availableChoice : availableChoices) {
            if (availableChoice.getChoiceId().equals(choiceId))
                return availableChoice;
        }
        return null;
    }

    private DraftSession resolveDraftSession(String code, Player player, boolean forPick) throws Exception {
        League league = findLeagueByType(code);
        if (league != null) {
            LeagueData leagueData = league.getLeagueData(_soloDraftDefinitions);
            int leagueStart = leagueData.getSeries().get(0).getStart();
            if (!leagueData.isSoloDraftLeague() || DateUtils.getCurrentDate() < leagueStart)
                return null;
            NewSoloDraftLeagueData soloDraftLeagueData = (NewSoloDraftLeagueData) leagueData;
            CollectionType collectionType = soloDraftLeagueData.getCollectionType();
            CardCollection collection = _collectionsManager.getPlayerCollection(player, collectionType.getCode());
            if (collection == null)
                return null;
            soloDraftLeagueData.repairExtraInformation(collection, player);
            return new DraftSession(collectionType, soloDraftLeagueData.getSoloDraft(), collection);
        }
        if (_hallServer == null)
            return null;
        CollectionType collectionType = _hallServer.getTournamentCollectionType(code);
        if (collectionType == null)
            return null;
        CardCollection collection = _collectionsManager.getPlayerCollection(player, collectionType.getCode());
        if (collection == null)
            return null;
        String draftType = _hallServer.getTournamentSoloDraftType(code);
        if (draftType == null && collection.getExtraInformation() != null)
            draftType = String.valueOf(collection.getExtraInformation().get("soloDraftType"));
        if (draftType == null || "null".equals(draftType))
            return null;
        SoloDraft soloDraft = _soloDraftDefinitions.getSoloDraft(draftType);
        if (soloDraft == null)
            return null;
        if (forPick && extraFlag(collection, "finished"))
            return null;
        return new DraftSession(collectionType, soloDraft, collection);
    }

    private static boolean extraFlag(CardCollection collection, String key) {
        if (collection == null || collection.getExtraInformation() == null)
            return false;
        Object value = collection.getExtraInformation().get(key);
        if (value instanceof Boolean)
            return (Boolean) value;
        return value != null && "true".equalsIgnoreCase(String.valueOf(value));
    }

    private static int extraInt(CardCollection collection, String key) {
        if (collection == null || collection.getExtraInformation() == null)
            return 0;
        Object value = collection.getExtraInformation().get(key);
        if (value instanceof Number)
            return ((Number) value).intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static long extraLong(CardCollection collection, String key) {
        if (collection == null || collection.getExtraInformation() == null)
            return 0;
        Object value = collection.getExtraInformation().get(key);
        if (value instanceof Number)
            return ((Number) value).longValue();
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static final class DraftSession {
        private final CollectionType collectionType;
        private final SoloDraft soloDraft;
        private final CardCollection collection;

        private DraftSession(CollectionType collectionType, SoloDraft soloDraft, CardCollection collection) {
            this.collectionType = collectionType;
            this.soloDraft = soloDraft;
            this.collection = collection;
        }
    }
}
