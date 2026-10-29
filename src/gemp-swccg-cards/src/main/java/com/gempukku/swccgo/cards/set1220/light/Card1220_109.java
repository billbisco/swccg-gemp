package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractDevice;
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
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Device
 * Title: Han's Toolkit
 */
public class Card1220_109 extends AbstractDevice {
    public Card1220_109() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Hans_Toolkit, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Experienced pilots keep a well equipped toolkit. Horizontal boosters, alluvial dampers and hydrospanners aid in performing high-tech repairs aboard starships.");
        setGameText("Deploy on Falcon or Han. While Han piloting Falcon at a battleground, non-immune to Alter Effects (except Emperor's Power, Presence of the Force, and Search And Destroy) are suspended.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Falcon, Filters.Han));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Falcon, Filters.Han);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Falcon, Filters.Han);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hanPilotingFalconAtBattleground = Filters.and(Filters.Han, Filters.piloting(Filters.Falcon), Filters.at(Filters.battleground));
        Filter effectsToSuspend = Filters.and(Filters.Effect, Filters.not(Filters.immune_to_Alter),
                Filters.not(Filters.or(Filters.title(Title.Emperors_Power), Filters.title(Title.Presence_Of_The_Force), Filters.title(Title.Search_And_Destroy))));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, effectsToSuspend, new OnTableCondition(self, hanPilotingFalconAtBattleground)));
        return modifiers;
    }
}
