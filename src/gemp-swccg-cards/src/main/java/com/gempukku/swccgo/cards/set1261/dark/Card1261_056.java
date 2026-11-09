package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.Collections;
import java.util.List;

/** Final Legacy master printing of Black1 (Juno Eclipse replaces original Black Leader). */
public class Card1261_056 extends AbstractStarfighter {
    public Card1261_056() {
        super(Side.DARK, 2, 2, 2, null, 5, 3, 5, "Black 1", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("TIE advanced x1 fighter boasting improved power plant, stronger shields, armored hull and enhanced weapons. Deployed to elite Imperial Navy pilots.");
        setGameText("May add Juno Eclipse as pilot. TIEs are immune to Don't Get Cocky. Once per turn, may use 1 Force to ▲ a Black Squadron TIE. Draws one battle destiny if not able to otherwise. Immune to attrition < 5.");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.TIE_ADVANCED_X1);
        addKeywords(Keyword.BLACK_SQUADRON);
        setPilotCapacity(1);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Juno;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToTitleModifier(self, Filters.TIE, Title.Dont_Get_Cocky),
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1),
                new ImmuneToAttritionLessThanModifier(self, 5));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__BLACK_1__UPLOAD_BLACK_SQUADRON_TIE;
        if (GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, id);
            action.setText("Take Black Squadron TIE into hand");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                    Filters.Black_Squadron_tie, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
