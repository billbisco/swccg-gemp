package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set9.dark.Card9_125;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PerTIEEvaluator;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;
import java.util.List;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Flagship Operations. */
public class Card15_182 extends Card9_125 {
    public Card15_182() {
        setLore("");
        setCredits(1);
        setGameText("Deploy on your Star Destroyer if at least 4 Executor sites on table. Your TIEs are deploy -1, forfeit +2 and destiny +1. At systems where you have a TIE, your Imperial capital starships are deploy -3 and your battle destiny draws are +1 each.");
        setAlternateDestiny(5);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) {
                addIcon(icon, 0);
            }
        }
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.REFLECTIONS_GOLD;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNKNOWN;
    }

    @Override
    public Float getDestiny() {
        return 5f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
                                                        PlayCardOptionId option, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Star_Destroyer);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self,
                                                       PlayCardOptionId option, boolean asReact) {
        return GameConditions.canSpotLocation(game, 4, Filters.Executor_site)
                || game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self,
                        ModifyGameTextType.FLAGSHIP_OPERATIONS__MAY_IGNORE_DEPLOYMENT_RESTRICTIONS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter ties = Filters.and(Filters.your(self), Filters.TIE, Filters.canBeTargetedBy(self));
        Filter systems = Filters.sameSystemAs(self, ties);
        return List.of(
                new DeployCostModifier(self, ties, new PerTIEEvaluator(-1)),
                new ForfeitModifier(self, ties, new PerTIEEvaluator(2)),
                new DestinyModifier(self, ties, new PerTIEEvaluator(1)),
                new DeployCostToLocationModifier(self,
                        Filters.and(Filters.your(self), Filters.Imperial_starship, Filters.capital_starship), -3, systems),
                new EachBattleDestinyModifier(self, systems, 1, self.getOwner()));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of();
    }
}
