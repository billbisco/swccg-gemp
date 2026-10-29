package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set2.light.Card2_056;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

/** Reflections Gold remaster; printed modes and symbols verified from the approved image. */
public class Card15_127 extends Card2_056 {
    public Card15_127() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(5);
        setLore("");
        setGameText("During your control phase, target one weapon, device, or vehicle at same site as your Undercover spy. Draw destiny. If destiny > target's deploy cost, target is lost (may be stolen instead if spy also a thief). OR Cancel Informant. OR If your weapon (except a lightsaber) was just fired during a battle, add one battle destiny.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Sabotage", "Slight Weapons Malfunction");
    }
    @Override public String getTitle() { return "Sabotage & Slight Weapons Malfunction"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }


    private static final MalfunctionActions MALFUNCTION = new MalfunctionActions();
    private static final class MalfunctionActions extends com.gempukku.swccgo.cards.set7.light.Card7_102 {
        List<PlayInterruptAction> actions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
            return super.getGameTextOptionalAfterActions(player, game, result, self);
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String player, SwccgGame game, EffectResult result, PhysicalCard self) {
        return MALFUNCTION.actions(player, game, result, self);
    }
}
