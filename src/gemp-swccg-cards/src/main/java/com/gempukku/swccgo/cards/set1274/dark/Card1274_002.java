package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Character
 * Subtype: Droid
 * Title: OOM Command Battle Droid
 */
public class Card1274_002 extends AbstractDroid {
    public Card1274_002() {
        super(Side.DARK, 3, 2, 0, 4, "OOM Command Battle Droid", Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setArmor(3);
        setLore("Commander. Leader.");
        setGameText("[Pilot] 2: any vehicle. While piloting a [Theed Palace] vehicle at a battleground site, adds one [Dark Side] and may add 1 to your Force drains here. Once per game, may take a card with \"fire\" in title into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.PRESENCE, Icon.EPISODE_I, Icon.THEED_PALACE);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER);
        addModelType(ModelType.BATTLE);
    }

    private Condition pilotingTheedPalaceVehicleAtBattlegroundSite(PhysicalCard self) {
        return new PilotingCondition(self, Filters.and(Icon.THEED_PALACE, Filters.vehicle, Filters.at(Filters.battleground_site)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.vehicle));
        modifiers.add(new IconModifier(self, Filters.and(Filters.location, Filters.sameLocation(self)),
                pilotingTheedPalaceVehicleAtBattlegroundSite(self), Icon.DARK_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.sameLocation(self))
                && pilotingTheedPalaceVehicleAtBattlegroundSite(self).isFulfilled(game.getGameState(), game.getModifiersQuerying())) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add 1 to Force drain");
            action.appendEffect(
                    new AddToForceDrainEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__OOM_COMMAND_BATTLE_DROID__TAKE_FIRE_CARD_INTO_HAND;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a card with \"fire\" in title into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.titleContains("fire"), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
