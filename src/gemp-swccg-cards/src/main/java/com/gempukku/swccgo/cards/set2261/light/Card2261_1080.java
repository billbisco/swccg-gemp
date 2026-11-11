package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.AbstractStartingEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.cards.set1261.light.Card1261_080;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Jabba, This Is Your Last Chance!
 * PC-E1 version of 1261_80: the PC card list (Holotable data) text (by 2010-05-29): lightsabers also may not 'swing' more than once per battle. The Virtual Cards Master List
 * (2013-04-27, PC-E3) repeats the release-sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1080 extends Card1261_080 {
    public Card2261_1080() {
        super();
        setGameText("Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If Quiet Mining Colony or Independent Operation on table, lightsabers may not add to Force drains or 'swing' more than once per battle. Cloud City Celebration is canceled.");
        setLegacyVersion(new LegacyVersion("1261_80", "PC-E1",
                "PC website card list (in the Holotable data by 2010-05-29)", LocalDate.of(2010, 5, 29), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected boolean limitsLightsaberSwings() {
        return true;
    }
}
