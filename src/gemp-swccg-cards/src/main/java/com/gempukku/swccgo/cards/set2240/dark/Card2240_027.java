package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_088;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Imperial Propaganda (V)
 * Original version of 1240_27: the Virtual Block 4 release sheet text (Dec 2009), without the clause "Non-[Virtual Block 4]
 * Imperial Propaganda is canceled." that the Virtual Cards Master List (Apr 2013) added. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_027 extends Card601_088 {
    public Card2240_027() {
        super();
        setGameText("If you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 2. Whenever you lose Force during opponent's control phase (except from a Force drain at a battleground or your card), that loss is cumulatively -1 (to a minimum of 1).");
        setLegacyVersion(new LegacyVersion("1240_27", "",
                "Virtual Block 4 release sheet (2009-12-07), card #26", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // No "Non-[Virtual Block 4] Imperial Propaganda is canceled." on this version
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = super.getGameTextRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
        if (actions == null)
            return null;
        List<RequiredGameTextTriggerAction> withoutCancel = new LinkedList<>(actions);
        // OTHER_CARD_ACTION_2 is the current card's "Non-[Virtual Block 4] Imperial Propaganda is canceled."
        withoutCancel.removeIf(action -> action.getGameTextActionId() == GameTextActionId.OTHER_CARD_ACTION_2);
        return withoutCancel;
    }
}
