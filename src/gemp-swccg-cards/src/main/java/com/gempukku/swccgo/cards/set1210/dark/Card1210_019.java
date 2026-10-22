package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CumulativeTotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: Reserve Pilot
 */
public class Card1210_019 extends AbstractImperial {
    public Card1210_019() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Reserve Pilot", Uniqueness.RESTRICTED_2, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Injuries kept Lord Vader's best TIE wingman out of the primary wave of starfighters. He remains on reserve duty, ready to replace lost pilots.");
        setGameText("Adds 2 to power of anything he pilots. While piloting at a system, your total battle destiny here is cumulatively +1. If a battle at a system just ended, may deploy (for free) aboard a starship from which you forfeited a pilot character.");
        addIcons(Icon.PILOT, Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingAtSystem = new AndCondition(new PilotingCondition(self), new AtCondition(self, Filters.system));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new CumulativeTotalBattleDestinyModifier(self, Filters.here(self), pilotingAtSystem, 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justForfeited(game, effectResult, playerId, Filters.and(Filters.character, Filters.pilot))) {
            PhysicalCard fromAttachedTo = ((LostFromTableResult) effectResult).getFromAttachedTo();
            if (fromAttachedTo != null
                    && Filters.starship.accepts(game, fromAttachedTo)
                    && GameConditions.isDuringBattleAt(game, Filters.system)) {
                WhileInPlayData data = self.getWhileInPlayData();
                if (data == null) {
                    data = new WhileInPlayData();
                    self.setWhileInPlayData(data);
                }
                if (!data.getPhysicalCards().contains(fromAttachedTo)) {
                    data.getPhysicalCards().add(fromAttachedTo);
                }
            }
            return null;
        }

        if (TriggerConditions.battleEndedAt(game, effectResult, Filters.system)
                && self.getWhileInPlayData() != null
                && !self.getWhileInPlayData().getPhysicalCards().isEmpty()) {
            List<PhysicalCard> starships = new LinkedList<PhysicalCard>(self.getWhileInPlayData().getPhysicalCards());
            Filter starshipFilter = Filters.in(starships);
            if (GameConditions.canSpot(game, self, starshipFilter)
                    && Filters.deployableToTarget(self, starshipFilter, true, 0).accepts(game, self)) {
                PlayCardAction playCardAction = getPlayCardAction(playerId, game, self, self, true, 0, null, null, null, null, null, false, 0, starshipFilter, null);
                if (playCardAction != null) {
                    playCardAction.appendEffect(
                            new PassthruEffect(playCardAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    self.setWhileInPlayData(null);
                                }
                            });
                    return Collections.singletonList(playCardAction);
                }
            }
        }
        return null;
    }
}
