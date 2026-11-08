package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_015;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Device
 * Title: Mandalorian Armor (V)
 * Pre-errata version of 1250_15: the Virtual Block 5 release sheet text (Dec 2009), "Used Pile if on The Mandalorian"
 * (the Master List of 2013-04-27 changed it to Jango). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_015 extends Card1250_015 {
    public Card2250_015() {
        setGameText("Deploy on your alien of ability < 4. Character gains bounty hunter skill and immunity to attrition < 4. If character just hit, may place this device in Lost Pile (Used Pile if on The Mandalorian) to restore it to normal.");
        setLegacyVersion(new LegacyVersion("1250_15", "",
                "Virtual Block 5 release sheet (2009-12-07), card #13", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard wearer = self.getAttachedTo();
        if (wearer != null && TriggerConditions.justHit(game, effectResult, wearer)) {
            var action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Restore wearer to normal");
            if (Filters.title("The Mandalorian").accepts(game, wearer))
                action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
            else
                action.appendCost(new PlaceCardInLostPileFromTableEffect(action, self));
            action.appendEffect(new RestoreCardToNormalEffect(action, wearer));
            return Collections.singletonList(action);
        }
        return null;
    }
}
