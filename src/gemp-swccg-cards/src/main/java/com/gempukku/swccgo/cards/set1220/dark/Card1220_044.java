package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Subtype: Immediate
 * Title: Pride Of The Empire
 */
public class Card1220_044 extends AbstractImmediateEffect {
    public Card1220_044() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Pride_Of_The_Empire, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial starships that perform with distinction are highly publicized in an attempt to make the pilots look like heroes to the citizens of the Empire.");
        setGameText("If you just deployed a TIE/SA, deploy on a unique TIE/SA. Bombing Run is Immune to Control. May place this card in Used Pile to take Bombing Run or ComScan Detection into hand from Reserve Deck; reshuffle (or, once per game, retrieve); opponent loses 1 Force. (Immune to Control.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.unique, Filters.TIE_sa);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, Filters.and(Filters.your(self), Filters.TIE_sa))) {
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0,
                    Filters.and(Filters.your(self), Filters.unique, Filters.TIE_sa), null);
            if (action != null) {
                action.setText("Deploy on a unique TIE/SA");
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Bombing_Run, Title.Control));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        String opponent = game.getOpponent(playerId);
        Filter takeFilter = Filters.or(Filters.Bombing_Run, Filters.title(Title.ComScan_Detection));

        GameTextActionId takeActionId = GameTextActionId.LEGACY__PRIDE_OF_THE_EMPIRE_V__TAKE_BOMBING_RUN;
        if (GameConditions.canSearchReserveDeck(game, playerId, self, takeActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, takeActionId);
            action.setText("Take Bombing Run or ComScan Detection into hand");
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, takeFilter, true));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        GameTextActionId retrieveActionId = GameTextActionId.LEGACY__PRIDE_OF_THE_EMPIRE_V__RETRIEVE;
        if (GameConditions.isOncePerGame(game, self, retrieveActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, retrieveActionId);
            action.setText("Retrieve Bombing Run or ComScan Detection");
            action.setActionMsg("Retrieve Bombing Run or ComScan Detection; opponent loses 1 Force");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, takeFilter));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }
        return actions.isEmpty() ? null : actions;
    }
}
