package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceBottomCardsOfLostPileOutOfPlayEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Title: 3,720 To 1
 */
public class Card1112_022 extends AbstractNormalEffect {
    public Card1112_022() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title._3720_To_1, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("What are the odds of having a Corellian, a Wookiee and a protocol droid together with an Alderaanian princess who happens to be the daughter of the Dark Lord of the Sith?");
        setGameText("Deploy on table. Droids are destiny +1. Once per turn, may place the bottom 2 cards in Lost Pile out of play to deploy (from Lost Pile) Forced Servitude, a character weapon, a location, or an Effect that deploys on a location (without 'lose' in game text). (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyModifier(self, Filters.droid, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__3720_TO_1_V__DEPLOY_FROM_LOST_PILE;
        Filter deployFilter = Filters.or(Filters.title(Title.Forced_Servitude), Filters.character_weapon, Filters.location,
                Filters.and(Filters.Effect, Filters.gameTextContains("location"), Filters.not(Filters.gameTextContains("lose"))));

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.numCardsInLostPile(game, playerId) >= 2
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a card from Lost Pile");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new PlaceBottomCardsOfLostPileOutOfPlayEffect(action, playerId, 2));
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, deployFilter, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
