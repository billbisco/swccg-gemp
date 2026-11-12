package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Stunning Leader
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Stunning Leader]] (Decipher archive scan); superseded by the Players Committee
 * errata, wiki [[Stunning Leader (PC Errata)]] (date unknown; upper bound March 2017). The Dark Side counterpart of
 * Blast The Door, Kid! with the same printed text: it excludes the leaders and characters of ability &gt; 2 that are
 * in the battle when it resolves (as Card2_045o); the errata's "for remainder of battle" exclusion of ones introduced
 * later is not part of the printed text.
 */
public class Card2_140o extends Card2_140 {
    public Card2_140o() {
        super();
        setGameText("After a battle at an interior site is initiated, use 1 Force to cause all leaders and all characters with ability > 2 (on both sides) to be completely excluded from that battle.");
        setTextState(new TextState("2_140", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.or(Filters.leader, Filters.and(Filters.character, Filters.abilityMoreThan(2))),
                Filters.participatingInBattle, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.interior_site)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final Collection<PhysicalCard> charactersToExclude = Filters.filterActive(game, self, filter);
            if (!charactersToExclude.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Exclude leaders and characters from battle");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                // Allow response(s)
                action.allowResponses("Exclude " + GameUtils.getAppendedNames(charactersToExclude) + " from battle",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s): the cards in the battle now; no lasting exclusion
                                action.appendEffect(
                                        new ExcludeFromBattleEffect(action, charactersToExclude));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
