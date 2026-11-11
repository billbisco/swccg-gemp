package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.set1261.light.Card1261_082;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.TitleComponent;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeSuspendedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NotHitWhenThrownModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Maneuvering Flaps & Nick Of Time
 * PC-E1 version of 1261_82: the PC website card list text (by 2012-07-21): no "May not be suspended.". The Virtual Cards Master List
 * (2013-04-27, PC-E3) repeats the release-sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1082 extends Card1261_082 {
    public Card2261_1082() {
        super();
        setGameText("Deploy on table. Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.' During your deploy phase, may reveal an unpiloted combat vehicle from hand to take its matching pilot character (or vice versa) from Reserve Deck and deploy both simultaneously; reshuffle. Whenever you play Desperate Tactics, Lucky Shot, On Target, Rapid Fire, or Steady Aim, may draw top card from Reserve Deck. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_82", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected boolean mayNotBeSuspended() {
        return false;
    }
}
