package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.evaluators.OccupiesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Character
 * Subtype: Alien
 * Title: Kebyc
 */
public class Card1106_012 extends AbstractAlien {
    public Card1106_012() {
        super(Side.LIGHT, 3, 3, 1, 2, 5, "Kebyc", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior accountant for the Cloud City Miner's Guild. Administrates the annual dues and fights corruption among her fellow guild members.");
        setGameText("Deploys free to Cloud City. You activate +1 Force for every Cloud City site you occupy. Your aliens are deploy -1 here. Opponent's aliens are deploy +1 here.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Cloud_City_location));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalForceGenerationModifier(self, new OccupiesEvaluator(playerId, Filters.Cloud_City_site), playerId));
        DeployCostToLocationModifier yourAliens = new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.alien), -1, Filters.sameLocation(self));
        yourAliens.setClauseIdentity("your-aliens");
        modifiers.add(yourAliens);
        DeployCostToLocationModifier opponentsAliens = new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.alien), 1, Filters.sameLocation(self));
        opponentsAliens.setClauseIdentity("opponents-aliens");
        modifiers.add(opponentsAliens);
        return modifiers;
    }
}
