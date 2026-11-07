package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.List;

/** Legacy Block 6.1 Dark Waters & Krayt Dragon Bones. */
public class Card1261_033 extends AbstractNormalEffect {
    public Card1261_033() {
        super(Side.DARK, 1, PlayCardZoneOption.ATTACHED, "Dark Waters & Krayt Dragon Bones", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        addComboCardTitles(Title.Dark_Waters, Title.Krayt_Dragon_Bones);
        setGameText("Deploy on Rancor Pit. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May [download] a non-[Selective] ferocious creature here. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId id, boolean asReact) {
        return Filters.Rancor_Pit;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var pit = self.getAttachedTo();
        if (pit == null)
            return null;
        if (TriggerConditions.justEatenAt(game, result, Filters.and(Filters.opponents(self), Filters.character), pit)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.setText("Retrieve 3 Force");
            action.appendEffect(new RetrieveForceEffect(action, self.getOwner(), 3));
            return List.of(action);
        }
        if (TriggerConditions.justLost(game, result, Filters.creature)) {
            var loss = (LostFromTableResult) result;
            if (loss.getFromLocation() != pit || loss.getCard().getBlueprint().getDeployCost() == null)
                return null;
            var creature = loss.getCard();
            int amount = (int) Math.ceil(creature.getBlueprint().getDeployCost() / 2f);
            if (amount <= 0)
                return null;
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.setText("Creature owner loses " + amount + " Force");
            action.appendEffect(new LoseForceEffect(action, creature.getOwner(), amount));
            return List.of(action);
        }
        return null;
    }
}
