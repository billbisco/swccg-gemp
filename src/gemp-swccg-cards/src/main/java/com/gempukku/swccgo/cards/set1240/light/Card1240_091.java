package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToCancelModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Uncharted Settlements
 */
public class Card1240_091 extends AbstractNormalEffect {
    public Card1240_091() {
        super(Side.LIGHT, 2, PlayCardZoneOption.ATTACHED, "Uncharted Settlements", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setGameText("Deploy on [Virtual Block 4] Hidden Base. Organized Attack, S-foils, Revolution, Your Destiny (unless you chose Rebel/Rebel Starship), and [Virtual Block 4] Ellorrs Madak are canceled. You may not deploy operatives or [Maintenance] cards. Force generation at Endor system may not be canceled. Unless Leave Them To Me on table, opponent's combat vehicles, Dark Jedi, [Maintenance] cards, and [Permanent Weapon] cards are deploy +2 to Rebel Cell sites. Opponent generates no Force at Hidden Landing Site. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_II);
        addImmuneToCardTitle(Title.Alter);
    }

    private static Filter virtualSuffix() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().hasVirtualSuffix();
            }
        };
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Hidden_Base, virtualSuffix());
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter rebelCellSites = Filters.titleContains("Rebel Cell");
        Condition choseRebelPair = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard hiddenBase = Filters.findFirstActive(gameState.getGame(), self, Filters.and(Filters.Hidden_Base, virtualSuffix()));
                return hiddenBase != null && hiddenBase.getWhileInPlayData() != null
                        && "Rebel".equals(hiddenBase.getWhileInPlayData().getTextValue());
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.Organized_Attack));
        modifiers.add(new SuspendsCardModifier(self, Filters.S_foils));
        modifiers.add(new SuspendsCardModifier(self, Filters.Revolution));
        modifiers.add(new SuspendsCardModifier(self, Filters.Your_Destiny, new UnlessCondition(choseRebelPair)));
        modifiers.add(new SuspendsCardModifier(self, Filters.and(Filters.Ellorrs_Madak, virtualSuffix())));
        modifiers.add(new MayNotDeployModifier(self, Filters.or(Filters.operative, Icon.MAINTENANCE), playerId));
        modifiers.add(new ForceGenerationImmuneToCancelModifier(self, Filters.Endor_system, Filters.any));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self),
                Filters.or(Filters.combat_vehicle, Filters.Dark_Jedi, Icon.MAINTENANCE, Icon.PERMANENT_WEAPON)),
                new UnlessCondition(new OnTableCondition(self, Filters.Leave_Them_To_Me)), 2, rebelCellSites));
        modifiers.add(new GenerateNoForceModifier(self, Filters.titleContains("Rebel Cell - Hidden Landing Site"), opponent));
        return modifiers;
    }
}
