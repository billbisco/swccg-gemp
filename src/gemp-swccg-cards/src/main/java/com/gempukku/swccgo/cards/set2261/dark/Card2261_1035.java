package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.AbstractStartingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetForRemainderOfGameDataEffect;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.cards.set1261.dark.Card1261_035;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: He's No Jedi
 * PC-E1 version of 1261_35: the PC card list (Holotable data) text (by 2010-05-29): lightsabers also may not 'swing' more than once per battle. The Virtual Cards Master List
 * (2013-04-27, PC-E3) repeats the release-sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1035 extends Card1261_035 {
    public Card2261_1035() {
        super();
        setGameText("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If This Deal Is Getting Worse All The Time on table, for remainder of game lightsabers may not add to Force Drains or 'swing' more than once per battle. Cloud City Occupation is canceled.");
        setLegacyVersion(new LegacyVersion("1261_35", "PC-E1",
                "PC website card list (in the Holotable data by 2010-05-29)", LocalDate.of(2010, 5, 29), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected boolean limitsLightsaberSwings() {
        return true;
    }
}
