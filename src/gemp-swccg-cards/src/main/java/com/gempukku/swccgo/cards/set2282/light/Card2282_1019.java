package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyIKnowThatLaugh;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.set1282.light.Card1282_019;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.StackOneCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Title: Rennek (V)
 * PC-E1 version of 1282_19: the PC website card list text (by 2012-12-12): "reduce a participating character's forfeit to 0" (not a character whose forfeit may not be reduced). The Virtual Cards Master List
 * (2013-04-27, PC-E2) repeats the release-sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1019 extends Card1282_019 {
    public Card2282_1019() {
        super();
        setGameText("May be stacked on I Know That Laugh from hand for 2 force. While stacked on I Know That Laugh, stack face-up and during any battle may draw destiny. If destiny < number of your aliens in battle, reduce a participating character's forfeit to 0.");
        setLegacyVersion(new LegacyVersion("1282_19", "PC-E1",
                "PC website card list (by 2012-12-12)", LocalDate.of(2012, 12, 12), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected Filter forfeitTargetFilter() {
        return Filters.and(Filters.character, Filters.participatingInBattle, Filters.forfeitMayBeReduced);
    }
}
