<?xml version="1.0" encoding="UTF-8"?>
<tileset name="hex" tilewidth="256" tileheight="222" tilecount="5" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <properties>
   <property name="available" type="bool" value="true"/>
   <property name="isAI" type="bool" value="false"/>
   <property name="player" type="int" value="1"/>
  </properties>
  <image width="256" height="222" source="hex_green.png"/>
 </tile>
 <tile id="1">
  <properties>
   <property name="available" type="bool" value="true"/>
   <property name="isAI" type="bool" value="true"/>
   <property name="player" type="int" value="2"/>
   <property name="strategy" value="RandomStrategy"/>
  </properties>
  <image width="256" height="222" source="hex_orange.png"/>
 </tile>
 <tile id="2">
  <properties>
   <property name="available" type="bool" value="true"/>
   <property name="isAI" type="bool" value="true"/>
   <property name="player" type="int" value="3"/>
   <property name="strategy" value="RandomStrategy"/>
  </properties>
  <image width="256" height="222" source="hex_pink.png"/>
 </tile>
 <tile id="3">
  <properties>
   <property name="available" type="bool" value="true"/>
   <property name="isAI" type="bool" value="false"/>
   <property name="player" type="int" value="0"/>
  </properties>
  <image width="256" height="222" source="hex_neutral.png"/>
 </tile>
 <tile id="4">
  <properties>
   <property name="available" type="bool" value="false"/>
   <property name="isAI" type="bool" value="false"/>
   <property name="player" type="int" value="0"/>
  </properties>
  <image width="256" height="222" source="hex_blue.png"/>
 </tile>
</tileset>
