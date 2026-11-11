package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.set1114.dark.Card1114_029;
import com.gempukku.swccgo.cards.set1250.dark.Card1250_001;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfForceDrainModifierEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForceDrainInitiatedResult;
import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Admiral's Order
 * Title: Nothing Can Get Through Our Shield (V)
 * PC-E1 version of 1250_1: the PC website card list text (by 2012-07-21): "his non-[Presence] characters and vehicles" (only the controlling player's cards get the discount). The Virtual Cards Master List
 * (2013-04-27, PC-E3) repeats the release-sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1001 extends Card1250_001 {
    public Card2250_1001() {
        super();
        setGameText("At each system a player controls with a capital starship, his non-[Presence] characters and vehicles deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains).");
        setLegacyVersion(new LegacyVersion("1250_1", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected boolean discountOnlyControllersCards() {
        return true;
    }
}
