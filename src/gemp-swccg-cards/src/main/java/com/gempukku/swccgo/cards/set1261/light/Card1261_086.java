package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.List;

/** Reflection (V), Block 6.1 release sheet and complete official printing. */
public class Card1261_086 extends AbstractNormalEffect {
    private static final Filter ENDOR_LEIA = Filters.and(Filters.Leia, Icon.ENDOR);

    public Card1261_086() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Reflection, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("It was hard to imagine the enormous losses the Alliance suffered during the Battle of Hoth. Leia contemplated what she could do to help the Rebellion recover.");
        setGameText("Deploy on [Endor] Leia. Leia's training destiny draws are +1. During your control phase, may use 3 Force (if Save You It Can on table, may use 1 Force instead) to retrieve 1 Force (cannot be reduced).");
        addIcons(Icon.DAGOBAH);
        addKeyword(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return ENDOR_LEIA;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(SwccgGame game, PhysicalCard self, PlayCardOptionId option) {
        return ENDOR_LEIA;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new EachTrainingDestinyModifier(self, Filters.jediTestTargetingApprentice(Filters.hasAttached(self)), 1));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) return null;
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        if (GameConditions.canUseForce(game, playerId, 3)) actions.add(retrieve(playerId, self, gameTextSourceCardId, 3));
        if (GameConditions.canSpot(game, self, Filters.Save_You_It_Can) && GameConditions.canUseForce(game, playerId, 1)) {
            actions.add(retrieve(playerId, self, gameTextSourceCardId, 1));
        }
        return actions;
    }

    private TopLevelGameTextAction retrieve(String playerId, PhysicalCard self, int gameTextSourceCardId, int cost) {
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
        action.setText("Use " + cost + " Force to retrieve 1 Force");
        action.appendUsage(new OncePerPhaseEffect(action));
        action.appendCost(new UseForceEffect(action, playerId, cost));
        action.appendEffect(new RetrieveForceEffect(action, playerId, 1) {
            @Override
            public boolean mayNotBeReduced() {
                return true;
            }
        });
        return action;
    }
}
