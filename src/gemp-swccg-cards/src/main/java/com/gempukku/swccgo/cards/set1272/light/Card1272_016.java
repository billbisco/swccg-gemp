package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Character
 * Subtype: Rebel
 * Title: Commander Wedge Antilles (V)
 */
public class Card1272_016 extends AbstractRebel {
    public Card1272_016() {
        super(Side.LIGHT, 3, 3, 3, 2, 6, "Commander Wedge Antilles", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Promoted to commander for his heroism in the Battle of Yavin. Leader in charge of training new pilots assigned to Echo Base. Piloted Rogue 3 in the battle of Hoth.");
        setGameText("Adds 3 to power of anything he pilots. Anything Wedge pilots is maneuver +1. Once per turn, may take One More Pass into hand from Reserve Deck; reshuffle. While piloting at a battleground site, draws one battle destiny if not able to otherwise, and [Virtual Block 4] Their Fire Has Gone Out Of The Universe flips and may not flip back.");
        addPersona(Persona.WEDGE);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.COMMANDER, Keyword.LEADER, Keyword.ROGUE_SQUADRON);
        setSpecies(Species.CORELLIAN);
        setMatchingVehicleFilter(Filters.Rogue_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingAtBattlegroundSite = new PilotingAtCondition(self, Filters.battleground_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingAtBattlegroundSite, 1));
        // Once flipped to [Virtual Block 4] Hunt Down And Destroy The Jedi, it may not flip back while Wedge pilots at a battleground site
        modifiers.add(new MayNotBeFlippedModifier(self, pilotingAtBattlegroundSite, Filters.and(Filters.Hunt_Down_And_Destroy_The_Jedi, Icon.LEGACY_BLOCK_4)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isPilotingAt(game, self, Filters.battleground_site)) {
            PhysicalCard theirFire = Filters.findFirstActive(game, self, Filters.and(Filters.Their_Fire_Has_Gone_Out_Of_The_Universe, Icon.LEGACY_BLOCK_4));
            if (theirFire != null && GameConditions.canBeFlipped(game, theirFire)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip " + GameUtils.getFullName(theirFire));
                action.setActionMsg("Flip " + GameUtils.getCardLink(theirFire));
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, theirFire));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COMMANDER_WEDGE_V__UPLOAD_ONE_MORE_PASS;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take One More Pass into hand from Reserve Deck");
            action.setActionMsg("Take One More Pass into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.One_More_Pass, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
