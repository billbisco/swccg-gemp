package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.DrawsNoMoreThanBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Bravado
 */
public class Card1281_034 extends AbstractNormalEffect {
    public Card1281_034() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Scoundrel's Bravado", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("");
        setGameText("Deploy from under Scoundrel's Luck on a scoundrel. Scoundrel (or starship they pilot) is defense value +2 and immune to attrition <5 (or adds 2 to immunity). Once per turn may lose 1 Force to add a battle destiny anywhere (you draw no more than two battle destiny there).");
        addIcons(Icon.SCOUNDREL);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        PhysicalCard stackedOn = self.getStackedOn();
        return stackedOn != null && Filters.title("Scoundrel's Luck").accepts(game, stackedOn);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.scoundrel;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.scoundrel;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter scoundrel = Filters.hasAttached(self);
        Filter starshipTheyPilot = Filters.and(Filters.starship, Filters.hasPiloting(self, scoundrel));
        Filter affect = Filters.or(scoundrel, starshipTheyPilot);
        Filter alreadyHasImmunity = Filters.alreadyHasImmunityToAttrition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, affect, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(affect, Filters.not(alreadyHasImmunity)), 5));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(affect, alreadyHasImmunity), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SCOUNDRELS_BRAVADO__ADD_BATTLE_DESTINY;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Add a battle destiny");
            action.setActionMsg("Add a battle destiny anywhere");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new LoseForceEffect(action, playerId, 1, true));
            action.appendEffect(new AddBattleDestinyEffect(action, 1, playerId));
            action.appendEffect(new DrawsNoMoreThanBattleDestinyEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
