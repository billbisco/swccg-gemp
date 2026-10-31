package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Character
 * Subtype: Rebel
 * Title: Jeroen Webb
 */
public class Card1112_014 extends AbstractRebel {
    public Card1112_014() {
        super(Side.LIGHT, 2, 2, 1, 2, 4, Title.Jeroen_Webb, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Native of Ralltiir. Spy for Ralltiir's underground network after his homeworld was subjugated.");
        setGameText("If deploying to opponent's mobile site, deploys -2 as an Undercover spy. Nevar Yalnal may not target spies here. Opponent's total influence here is -1 for each of your spies at sites.");
        setArmor(4);
        addIcons(Icon.HOTH, Icon.PILOT);
        addKeywords(Keyword.SPY);
        setMayDeployAsUndercoverSpy(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.and(Filters.opponents(self), Filters.mobile_site)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.spy, Filters.here(self)), Filters.title(Title.Nevar_Yalnal)));
        String opponent = game.getOpponent(self.getOwner());
        modifiers.add(new TotalInfluenceAtLocationModifier(self, Filters.here(self), null, new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
                return -Filters.countActive(game, self, Filters.and(Filters.your(self), Keyword.SPY, Filters.at(Filters.site)));
            }

            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard other) {
                return evaluateExpression(gameState, modifiersQuerying, card);
            }
        }, opponent));
        return modifiers;
    }
}
