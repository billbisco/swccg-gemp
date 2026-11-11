package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.set1261.light.Card1261_081;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Incom Corporation & Koensayr Manufacturing
 * PC-E1 version of 1261_81: the PC website card list text (by 2012-07-21): no "Echo Base Operations is canceled.". The Virtual Cards Master List
 * (2013-04-27, PC-E3) repeats the release-sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1081 extends Card1261_081 {
    public Card2261_1081() {
        super();
        setGameText("Deploy on a Rebel Base war room. Your ability-1 permanent pilots aboard T-47s, X-wings, Y-wings, and Z-95s (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. During your control phase, may retrieve one Y-wing or Z-95. Suspended if opponent controls this site. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_81", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected boolean cancelsEchoBaseOperations() {
        return false;
    }
}
