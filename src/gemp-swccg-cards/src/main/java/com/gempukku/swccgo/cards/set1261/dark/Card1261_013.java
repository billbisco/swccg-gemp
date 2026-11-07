package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyForfeitEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 Moff Tarkin, Death Star Commandant. */
public class Card1261_013 extends AbstractImperial {
    public Card1261_013() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Moff Tarkin, Death Star Commandant", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Leader.");
        setGameText("Adds 1 to power of anything he pilots. Deploys -2 to Death Star. Your Force generation at Eriadu is +2. Superlaser fires at starships for free, and targets it hits are forfeit -4. Wherever you have an Imperial, you may initiate battles for free. Immune to attrition < 3.");
        addPersona(Persona.TARKIN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.SPECIAL_EDITION);
        addKeywords(Keyword.LEADER, Keyword.MOFF);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Death_Star));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 1),
                new ForceGenerationModifier(self, Filters.and(Filters.system, Filters.title(Title.Eriadu)), 2, self.getOwner()),
                // All firing modes of Superlaser target starships; planet destruction uses a separate Epic Event cost.
                new FiresForFreeModifier(self, Filters.Superlaser),
                new InitiateBattlesForFreeModifier(self, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Imperial)), self.getOwner()),
                new ImmuneToAttritionLessThanModifier(self, 3));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int sourceId) {
        if (TriggerConditions.justHitBy(game, result, Filters.starship, Filters.Superlaser)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
            action.setText("Reduce hit starship's forfeit by 4");
            action.appendEffect(new ModifyForfeitEffect(action, ((HitResult) result).getCardHit(), -4));
            return Collections.singletonList(action);
        }
        return null;
    }
}
