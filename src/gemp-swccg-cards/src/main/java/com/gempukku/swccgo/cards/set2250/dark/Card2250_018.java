package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_018;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: An Enemy Of The Republic
 * Pre-errata version of 1250_18: the Virtual Block 5 release sheet text (Dec 2009). Opponent relocates it to any of
 * their participating characters (droids included); the "non-droid" GEMP plays now comes from an undated PC text.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_018 extends Card1250_018 {
    public Card2250_018() {
        setGameText("Deploy on Sidious. While at Senate, if battle was just initiated anywhere, opponent must relocate this card to their participating character; during battle here, your destiny draws may not be canceled. If character about to be lost, relocate to Sidious; may ▲ a card.");
        setLegacyVersion(new LegacyVersion("1250_18", "",
                "Virtual Block 5 release sheet (2009-12-07), card #15", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
