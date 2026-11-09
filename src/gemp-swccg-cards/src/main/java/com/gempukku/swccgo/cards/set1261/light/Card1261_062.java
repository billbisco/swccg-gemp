package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Baragwin (V), final Virtual Block 6.1 text. */
public class Card1261_062 extends AbstractAlien {
    public Card1261_062() {
        super(Side.LIGHT, 3, 2, 1, 2, 5, "Baragwin", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Hunchbacked scavengers. Found in separate groups throughout the galaxy. Good at finding missing items. Make excellent gun runners. Some work with the Rebellion.");
        setGameText("This character is lost if a Starting Effect on table. Your non-alien characters at sites are power and forfeit = 0. If present at a battleground site, your Force drains at battleground sites where you have two aliens are +1, and whenever you deploy an alien, retrieve 1 Force.");
        addIcons(Icon.JABBAS_PALACE); addKeywords(Keyword.SCAVENGER); setSpecies(Species.BARAGWIN);
    }

    /** PC-E1 (card list, 2012): "Your non-alien characters at sites are power and forfeit = 0." */
    protected Filter nonAliensReset(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.alien), Filters.at(Filters.site));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourNonAliens = nonAliensReset(self);
        Filter twoAliens = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
                return Filters.canSpot(gameState.getGame(), self, 2,
                        Filters.and(Filters.your(self), Filters.alien, Filters.at(location)));
            }
        };
        return Arrays.asList(new ResetPowerModifier(self, yourNonAliens, 0),
                new ResetForfeitModifier(self, yourNonAliens, 0),
                new ForceDrainModifier(self, Filters.and(Filters.battleground_site, twoAliens),
                        new PresentAtCondition(self, Filters.battleground_site), 1, self.getOwner()));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.Starting_Effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lose Baragwin"); action.appendEffect(new LoseCardFromTableEffect(action, self)); actions.add(action);
        }
        if (TriggerConditions.justDeployed(game, effectResult, self.getOwner(), Filters.alien)
                && GameConditions.isPresentAt(game, self, Filters.battleground_site)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force"); action.appendEffect(new RetrieveForceEffect(action, self.getOwner(), 1)); actions.add(action);
        }
        return actions;
    }
}
