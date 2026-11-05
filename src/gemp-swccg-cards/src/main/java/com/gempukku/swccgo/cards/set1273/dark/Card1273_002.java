package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAwayAsReactEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Pellaeon
 */
public class Card1273_002 extends AbstractImperial {
    /** Justifier (Virtual Block 6.3) is matched by title. */
    static final Filter JUSTIFIER = Filters.and(Filters.starship, Filters.title("Justifier"));

    public Card1273_002() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Admiral Pellaeon", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLore("Corellian leader. Lied about his age to enter the Academy. Took command of Chimaera when its captain was killed at the Battle of Endor. Admires Grand Admiral Thrawn.");
        setGameText("Adds 2 to power of anything he pilots. May be revealed from hand to take Justifier into hand from Reserve Deck; reshuffle and deploy both simultaneously (for -1 Force each). Opponent's passengers here are forfeit = 0. Once per game, if aboard Justifier, may lose 1 Force to move away from battle as a 'react' (at hyperspeed +2).");
        addIcons(Icon.PILOT);
        addKeywords(Keyword.LEADER, Keyword.ADMIRAL);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ADMIRAL_PELLAEON__DEPLOY_WITH_JUSTIFIER;

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal to deploy with Justifier");
            action.setActionMsg("Reveal " + GameUtils.getCardLink(self) + " to deploy simultaneously with Justifier from Reserve Deck");
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, self, JUSTIFIER, -1, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ResetForfeitModifier(self, Filters.and(Filters.opponents(self), Filters.character,
                Filters.aboardAsPassenger(Filters.any), Filters.here(self)), 0));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ADMIRAL_PELLAEON__MOVE_JUSTIFIER_AWAY;
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.and(JUSTIFIER, Filters.hasAboard(self)))) {
            final PhysicalCard justifier = Filters.findFirstActive(game, self, Filters.and(JUSTIFIER, Filters.hasAboard(self),
                    Filters.presentInBattle, Filters.notPreventedFromParticipatingInReact,
                    Filters.canMoveAsReactAsActionFromOtherCard(self, false, 0, true)));
            if (justifier != null) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Move Justifier away as a 'react'");
                action.setActionMsg("Move " + GameUtils.getCardLink(justifier) + " away from battle as a 'react' at hyperspeed +2");
                action.appendUsage(
                        new OncePerGameEffect(action));
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1, true));
                action.appendEffect(
                        new AddUntilEndOfBattleModifierEffect(action,
                                new HyperspeedModifier(self, justifier, 2), null));
                action.appendEffect(
                        new MoveAwayAsReactEffect(action, justifier, false));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
