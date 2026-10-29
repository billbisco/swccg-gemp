package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;
import java.util.List;

/** Reflections Gold: Projection Of A Skywalker & Yarna d'al' Gargan. */
public class Card15_047 extends AbstractNormalEffect {
    public Card15_047() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Projection Of A Skywalker & Yarna d'al' Gargan", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setTitleComponents(new TitleComponent(Title.Projection_Of_A_Skywalker, Uniqueness.RESTRICTED_2),
                new TitleComponent(Title.Yarna_dal_Gargan, Uniqueness.UNIQUE));
        setGameText("Deploy on any planet site. Opponent's Force drains are -1 here. Also, while you have fewer than 13 cards in hand, your non-unique cards in hand (except Effects of any kind and Interrupts) are immune to Monnok. (Immune to Alter while you occupy 2 battlegrounds.)");
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.planet_site;
    }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ForceDrainModifier(self, Filters.here(self), -1, game.getOpponent(self.getOwner())),
                new ImmuneToTitleModifier(self, Filters.and(Filters.inHand(self.getOwner()), Filters.non_unique,
                        Filters.not(Filters.Effect_of_any_Kind), Filters.not(Filters.Interrupt)),
                        new CardsInHandFewerThanCondition(self.getOwner(), 13), Title.Monnok));
    }
    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToTitleModifier(self, new OccupiesCondition(self.getOwner(), 2, Filters.battleground), Title.Alter));
    }
}
