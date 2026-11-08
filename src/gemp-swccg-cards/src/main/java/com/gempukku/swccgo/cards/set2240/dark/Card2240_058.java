package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set601.dark.Card601_087;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceTargetingCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 * Original version of 1240_58 (front): the Virtual Block 4 release sheet text (Dec 2009). "Whenever a character hit by
 * Galen's Lightsaber or Vader's Lightsaber leaves table, opponent loses 2 Force" has no time limit (AR 2.0 Appendix A, by
 * Aug 2012, limited it to "on the same turn"), and the back side has no "or if neither Vader nor Galen is on table" flip
 * clause. "May ▲ Galen's Fighter" is the starship later renamed Rogue Shadow. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_058 extends Card601_087 {
    public Card2240_058() {
        super();
        setGameText("Deploy Coruscant system, Imperial City, and A Sith's Plans. May also deploy If The Trace Was Correct.\n" +
                "For remainder of game, you may not deploy [Episode I] Dark Jedi. Whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table, opponent loses 2 Force.\n" +
                "While this side up, may ▲ Galen's Fighter. Galen's immunity to attrition is +2 for each Jedi present.\n" +
                "Flip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site.");
        setLegacyVersion(new LegacyVersion("1240_58", "",
                "Virtual Block 4 release sheet (2009-12-07), card #57", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 8, 27)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.or(Filters.Galen, Filters.Vader), Filters.at(Filters.battleground_site)))
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.unique, Filters.character, Filters.abilityMoreThan(3), Filters.at(Filters.battleground_site)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        RequiredGameTextTriggerAction hitAction = getHitCharacterLeavesTableTrigger(game, effectResult, self, gameTextSourceCardId);
        if (hitAction != null)
            actions.add(hitAction);
        return actions;
    }

    /**
     * "Whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table, opponent loses 2 Force." (both
     * sides of the original text). Unlike the current card (watched until the end of the turn: "on the same turn"), the hit
     * character is watched for the rest of the game while this objective is on table. Each hit character can make opponent
     * lose Force once (a character that leaves table is no longer hit). A character hit outside battle is also lost by rule
     * in response to the same hit; if that resolves first, opponent loses 2 Force right away (Bill, 2026-11-07).
     */
    static RequiredGameTextTriggerAction getHitCharacterLeavesTableTrigger(SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        if (!TriggerConditions.justHitByEvenIfLeftTable(game, effectResult, Filters.and(Filters.opponents(self), Filters.character), Filters.or(Persona.GALENS_LIGHTSABER, Persona.VADERS_LIGHTSABER)))
            return null;
        PhysicalCard justHitCard = ((HitResult) effectResult).getCardHit();
        if (justHitCard == null)
            return null;

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
        action.skipInitialMessageAndAnimation();
        action.setSingletonTrigger(true);
        action.setActionMsg(null);

        final int permCardIdSelf = self.getPermanentCardId();
        final int permCardIdHitCharacter = justHitCard.getPermanentCardId();
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                PhysicalCard hitCharacter = game.findCardByPermanentId(permCardIdHitCharacter);
                if (hitCharacter != null && !Filters.onTable.accepts(game, hitCharacter)) {
                    // Already left table (lost by the hit-outside-battle rule resolved first).
                    PhysicalCard objective = game.findCardByPermanentId(permCardIdSelf);
                    game.getGameState().sendMessage(GameUtils.getCardLink(objective) + " makes " + game.getOpponent(objective.getOwner())
                            + " lose 2 Force because " + GameUtils.getCardLink(hitCharacter) + ", hit by Galen's Lightsaber or Vader's Lightsaber, left table");
                    action.appendEffect(new LoseForceTargetingCardEffect(action, game.getOpponent(objective.getOwner()), 2, hitCharacter));
                    return;
                }
                final boolean[] done = {false};
                action.appendEffect(new AddUntilEndOfGameActionProxyEffect(action, new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        List<TriggerAction> actions1 = new LinkedList<TriggerAction>();
                        if (done[0])
                            return actions1;

                        PhysicalCard objective = game.findCardByPermanentId(permCardIdSelf);
                        PhysicalCard hitCharacter = game.findCardByPermanentId(permCardIdHitCharacter);

                        if (TriggerConditions.leavesTable(game, effectResult, Filters.samePermanentCardId(hitCharacter))) {
                            done[0] = true;
                            if (objective == null || !Filters.onTable.accepts(game, objective))
                                return actions1;
                            RequiredGameTextTriggerAction action1 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__HUNT_DOWN_V__HIT_CHARACTER_LEAVES_TABLE_LOSE_2_FORCE);
                            action1.setPerformingPlayer(objective.getOwner());
                            action1.setSingletonTrigger(true);
                            action1.setText("Lose 2 Force");
                            action1.setActionMsg("Lose 2 Force whenever a character hit by Galen's Lightsaber or Vader's Lightsaber leaves table");
                            action1.appendEffect(new LoseForceTargetingCardEffect(action1, game.getOpponent(objective.getOwner()), 2, hitCharacter));
                            actions1.add(action1);
                        }
                        return actions1;
                    }
                }));
            }
        });
        return action;
    }

    @Override
    protected String optionalDeployTitle() {
        return Title.If_The_Trace_Was_Correct;
    }
}
