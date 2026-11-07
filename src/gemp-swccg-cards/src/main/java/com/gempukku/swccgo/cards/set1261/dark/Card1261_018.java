package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 Thok & Thug. */
public class Card1261_018 extends AbstractAlien {
    public Card1261_018() {
        super(Side.DARK, 1, 4, 6, 1, 6, "Thok & Thug", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        addComboCardTitles("Thok", "Thug");
        setArmor(4);
        setSpecies(Species.GAMORREAN);
        addKeywords(Keyword.GUARD);
        addIcons(Icon.WARRIOR, Icon.REFLECTIONS_III);
        setLore("Jabba the Hutt knew that having a big, strong, dumb guard was good, but having lots of them is better.");
        setGameText("Deploys -1 to Jabba's Palace sites. While with your alien leader, Heroic. Once per turn, if defending a battle, may use 1 Force to add 2 to your total power.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DeployCostToLocationModifier(self, -1, Filters.Jabbas_Palace_site));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new KeywordModifier(self, self,
                new WithCondition(self, Filters.and(Filters.your(self), Filters.alien_leader)), Keyword.HEROIC));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleInitiatedBy(game, game.getOpponent(playerId))
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canUseForce(game, playerId, 1)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add 2 to total power");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new ModifyTotalPowerUntilEndOfBattleEffect(action, 2, playerId, "Adds 2 to total power"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
