package com.gempukku.swccgo.cards.set2220.light;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
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
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Alien
 * Title: Kebyc
 * Original version of 1220_93 (Virtual Block 2 release sheet, Dec 2009): "You generate +1 Force". GEMP has no separate activation bonus (activation follows total Force generation), so it plays like the later "activate" text. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_093 extends AbstractAlien {
    public Card2220_093() {
        super(Side.LIGHT, 3, 3, 1, 2, 5, "Kebyc", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior accountant for the Cloud City Miner's Guild. Administrates the annual dues and fights corruption among her fellow guild members.");
        setGameText("Deploys free to Cloud City. You generate +1 Force for every Cloud City site you occupy. Your aliens are deploy -1 here. Opponent's aliens are deploy +1 here.");
        setLegacyVersion(new LegacyVersion("1220_93", "",
                "Virtual Block 2 release sheet (2009-12-07), card #93; superseded by the PC card list errata (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
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
