package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6.3: TIE Avenger (V). */
public class Card1263_019 extends AbstractStarfighter {
    private static final Filter FACTORY = Filters.title("TIE Fighter Construction Facility");

    public Card1263_019() {
        super(Side.DARK, 3, 3, 3, null, 4, 2, 4, "TIE Avenger", Uniqueness.UNRESTRICTED,
                ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Design based on Lord Vader's prototype Advanced x1. Manufactured until replaced by the more economical TIE Interceptor. Equipped with deflector shields and a hyperdrive.");
        setGameText("May add 1 pilot and 1 astromech. While TIE Fighter Construction Facility on table, pilot is forfeit +2 and this starship may not be lost before the damage segment of battle. May deploy an astromech aboard from Reserve Deck; reshuffle.");
        addIcons(Icon.DAGOBAH, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6);
        addModelType(ModelType.TIE_AD);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, PhysicalCard self) {
        return List.of(new ForfeitModifier(self, Filters.piloting(self), new OnTableCondition(self, FACTORY), 2));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__TIE_AVENGER__DEPLOY_ASTROMECH;
        if (!GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)
                || game.getGameState().getAvailablePassengerCapacityForAstromech(game.getModifiersQuerying(), self, null) == 0) return null;
        var action = new TopLevelGameTextAction(self, sourceId, id);
        action.setText("Deploy an astromech aboard TIE Avenger");
        action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, Filters.astromech_droid, Filters.sameCardId(self), true));
        return List.of(action);
    }
}
