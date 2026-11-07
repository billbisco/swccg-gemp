package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/** Legacy Block 6.1 Royal Escort (V). */
public class Card1261_038 extends AbstractNormalEffect {
    public Card1261_038() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Royal Escort",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("When away from the Imperial Palace on Coruscant, the Emperor is protected by legions of troops. Typically this force includes soldiers trained to fight in local environment.");
        setGameText("Deploy on table unless your [Reflections 2] Objective on table. Unique (•) [Special Edition] Coruscant sites gain one [Light Side Force]. 'Emperor' on Royal Guards may be treated as 'Sidious.' Attrition against opponent is +1 for each battleground site occupied by two Royal Guards. May use 1 Force to [download] a Force Pike. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self,
            PlayCardOptionId option, boolean asReact) {
        return !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), CardCategory.OBJECTIVE,
                Icon.REFLECTIONS_II));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter specialEdition = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
                return card.getBlueprint().hasIcon(Icon.SPECIAL_EDITION);
            }
        };
        Filter sites = Filters.and(Filters.Coruscant_site, Filters.unique, specialEdition);
        return List.of(new IconModifier(self, sites, Icon.LIGHT_FORCE),
                new AttritionModifier(self, new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState state, ModifiersQuerying modifiers, PhysicalCard battleSite) {
                        int occupied = 0;
                        for (PhysicalCard site : Filters.filterTopLocationsOnTable(state.getGame(),
                                Filters.and(Filters.battleground_site, Filters.occupies(self.getOwner())))) {
                            if (Filters.countActive(state.getGame(), self, Filters.and(Filters.your(self),
                                    Filters.Royal_Guard, Filters.presentAt(site))) >= 2)
                                occupied++;
                        }
                        return occupied;
                    }
                }, game.getOpponent(self.getOwner())));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__ROYAL_ESCORT_V__DEPLOY_FORCE_PIKE;
        if (!GameConditions.canUseForce(game, playerId, 1)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id))
            return null;
        var action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Use 1 Force to deploy a Force Pike");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title("Force Pike"), true));
        return List.of(action);
    }
}
