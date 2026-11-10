package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set601.dark.Card601_087_BACK;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 * Original version of 1240_58 (back): the Virtual Block 4 release sheet text (Dec 2009). Flips back only if opponent has a
 * unique (•) character of ability &gt; 3 present at a battleground site (no "or if neither Vader nor Galen is on table").
 * See {@link Card2240_058}. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_058_BACK extends Card601_087_BACK {
    public Card2240_058_BACK() {
        super();
        setGameText("While this side up, opponent's Force drain bonuses are canceled and your Force drains at battlegrounds may not be canceled. During your control phase, may retrieve 1 Force. During your control phase, may use 2 Force to ▲ any one card without ability. If targeting a vehicle or starship with Lightsaber Parry, Tarkin's Orders, or There Is No Conflict, that destiny draw is +2.\n" +
                "Flip this card if opponent has a unique (•) character of ability > 3 present at a battleground site.");
        setLegacyVersion(new LegacyVersion("1240_58_BACK", "",
                "Virtual Block 4 release sheet (2009-12-07), card #57; superseded by the PC forum errata (2011-09-12)", LocalDate.of(2009, 12, 7), LocalDate.of(2011, 9, 12)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.unique, Filters.character, Filters.abilityMoreThan(3), Filters.at(Filters.battleground_site)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }

        RequiredGameTextTriggerAction hitAction = Card2240_058.getHitCharacterLeavesTableTrigger(game, effectResult, self, gameTextSourceCardId);
        return hitAction != null ? Collections.singletonList(hitAction) : null;
    }
}
