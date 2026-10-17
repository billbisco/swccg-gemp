package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalInfluenceAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: Jeroen Webb
 */
public class Card1220_092 extends AbstractRebel {
    public Card1220_092() {
        super(Side.LIGHT, 2, 2, 1, 2, 4, "Jeroen Webb", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("Native of Ralltiir. Spy for Ralltiir's underground network after his homeworld was subjugated.");
        setGameText("If deploying to opponent's mobile site, deploys as an Undercover spy (deploy -2). Nevar Yalnal may not target spies at Webb's site. Opponent's total influence here is -1 for each of your spies at sites.");
        addIcons(Icon.PILOT, Icon.HOTH);
        addKeywords(Keyword.SPY);
        // The undercover prompt is offered at any site. Printed text only does this at an opponent's mobile site.
        setMayDeployAsUndercoverSpy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.and(Filters.opponents(self), Filters.mobile_site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.spy, Filters.atSameSite(self)), Filters.Nevar_Yalnal));
        modifiers.add(new TotalInfluenceAtLocationModifier(self, Filters.here(self), null, spiesAtSites(self), opponent));
        return modifiers;
    }

    private Evaluator spiesAtSites(final PhysicalCard self) {
        return new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                int spies = Filters.countActive(gameState.getGame(), self,
                        Filters.and(Filters.your(self), Filters.spy, Filters.at(Filters.site)));
                return -spies;
            }

            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return evaluateExpression(gameState, modifiersQuerying, cardAffected);
            }
        };
    }
}
