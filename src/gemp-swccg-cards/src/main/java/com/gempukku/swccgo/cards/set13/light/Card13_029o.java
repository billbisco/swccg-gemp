package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.ResetTotalAbilityModifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Rebel
 * Title: Leia, Rebel Princess
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The printed text keeps opponent's total ability at Leia's site only while no opponent's non-alien character is
 * <i>present</i> there (a character aboard a starship or vehicle at the site is not present). The Players Committee
 * changed "present" to "here" (current text, Card13_029; earliest source found: the PC Decipher Errata sheet,
 * "Official text as of June 1, 2017"; 2023 Advanced Rulebook Appendix A).
 * Text from wiki.swccg.com [[Leia, Rebel Princess]].
 */
public class Card13_029o extends Card13_029 {
    public Card13_029o() {
        super();
        setGameText("Twice per game, may place top card of Lost Pile on top of Reserve Deck to cancel a Force drain at a related site. Unless opponent's non-alien character present, opponent's total ability at same site = 0. Immune to attrition < 5 if with Luke, Han or Jabba.");
        setTextState(new TextState("13_29", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetTotalAbilityModifier(self, Filters.sameSite(self),
                new AndCondition(new CantSpotCondition(self, Filters.and(Filters.opponents(self), Filters.non_alien_character, Filters.present(self))),
                        new NotCondition(new GameTextModificationCondition(self, ModifyGameTextType.LEIA_REFLECTIONS_III__MAY_NOT_CANCEL_FORCE_DRAINS_OR_REDUCE_ABILITY))),
                0, game.getOpponent(self.getOwner())));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new WithCondition(self, Filters.or(Filters.Luke, Filters.Han, Filters.Jabba)), 5));
        return modifiers;
    }
}
