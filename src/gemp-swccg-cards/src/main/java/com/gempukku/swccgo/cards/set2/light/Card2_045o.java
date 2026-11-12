package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Title: Blast The Door, Kid!
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * The printed text excludes the leaders and characters of ability &gt; 2 that are in the battle when it resolves.
 * The rule that cards introduced to the battle later are excluded too came with Decipher's 2002 Glossary Supplement
 * (state d, Card2_045d) and stays in the current text (Card2_045), so this state has no lasting exclusion.
 */
public class Card2_045o extends AbstractLostInterrupt {
    public Card2_045o() {
        super(Side.LIGHT, 4, Title.Blast_The_Door_Kid, Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("When Vader turned his attention to the escaping Rebels, Han immediately offered Luke some sage advice.");
        setGameText("After a battle at an interior site is initiated, use 1 Force to cause all leaders and all characters with ability > 2 (on both sides) to be completely excluded from that battle.");
        addIcons(Icon.A_NEW_HOPE);
        setTextState(new TextState("2_45", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(1998, 9, 1)));
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
