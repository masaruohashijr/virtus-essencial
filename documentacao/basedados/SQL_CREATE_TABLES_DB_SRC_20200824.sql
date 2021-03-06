/*============================================================================*/
/* DBMS: MS SQL Server 2017*/
/* Created on : 09/27/2020 20:09:37                                           */
/*============================================================================*/


/*============================================================================*/
/*                                 SCHEMAS                                    */
/*============================================================================*/
IF NOT EXISTS (SELECT NAME FROM SYS.SCHEMAS WHERE NAME ='SRC')
EXEC('CREATE SCHEMA [SRC]')
GO

/*============================================================================*/
/*                                  TABLES                                    */
/*============================================================================*/
CREATE TABLE [SRC].[CARTEIRA] (
  [ID_CARTEIRA]       INT NOT NULL IDENTITY(1,1),
  [ID_USER]           INT NOT NULL,
  [ID_STATUS]         INT NOT NULL,
  [TITULO]            VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]        VARCHAR(20),
  [PESO]              INT,
  [DT_CRIACAO]        DATETIME,
CONSTRAINT [PK_CARTEIRA] PRIMARY KEY ([ID_CARTEIRA])
)
GO

CREATE TABLE [SRC].[EQUIPE] (
  [ID_EQUIPE]        INT NOT NULL IDENTITY(1,1),
  [ID_USER]          INT NOT NULL,
  [ID_STATUS]        INT NOT NULL,
  [TITULO]           VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]       VARCHAR(20),
  [PESO]             INT,
  [DT_CRIACAO]       DATETIME,
CONSTRAINT [PK_EQUIPE] PRIMARY KEY ([ID_EQUIPE])
)
GO

CREATE TABLE [SRC].[PLANO] (
  [ID_PLANO]         INT NOT NULL IDENTITY(1,1),
  [ID_USER]          INT NOT NULL,
  [ID_STATUS]        INT NOT NULL,
  [TITULO]           VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]       VARCHAR(20),
  [PESO]             INT,
  [DT_CRIACAO]       DATETIME,
CONSTRAINT [PK_PLANO] PRIMARY KEY ([ID_PLANO])
)
GO

CREATE TABLE [SRC].[ENTIDADE] (
  [ID_ENTIDADE]       INT NOT NULL IDENTITY(1,1),
  [ID_STATUS]         INT NOT NULL,
  [ID_USER]           INT NOT NULL,
  [TITULO]            VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]        VARCHAR(20),
  [PESO]              INT,
  [DT_CRIACAO]        DATETIME,
CONSTRAINT [PK_ENTIDADE] PRIMARY KEY ([ID_ENTIDADE])
)
GO

CREATE TABLE [SRC].[CICLO] (
  [ID_CICLO]         INT NOT NULL IDENTITY(1,1),
  [ID_STATUS]        INT NOT NULL,
  [ID_USER]          INT NOT NULL,
  [TITULO]           VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]       VARCHAR(20),
  [PESO]             INT,
  [DT_CRIACAO]       DATETIME,
CONSTRAINT [PK_CICLO] PRIMARY KEY ([ID_CICLO])
)
GO

CREATE TABLE [SRC].[MATRIZ] (
  [ID_MATRIZ]        INT NOT NULL IDENTITY(1,1),
  [ID_USER]          INT NOT NULL,
  [ID_STATUS]        INT NOT NULL,
  [TITULO]           VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]       VARCHAR(20),
  [PESO]             INT,
  [DT_CRIACAO]       DATETIME,
CONSTRAINT [PK_MATRIZ] PRIMARY KEY ([ID_MATRIZ])
)
GO

CREATE TABLE [SRC].[COMPONENTE] (
  [ID_COMPONENTE]       INT NOT NULL IDENTITY(1,1),
  [ID_STATUS]           INT NOT NULL,
  [ID_USER]             INT NOT NULL,
  [TITULO]              VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]          VARCHAR(20),
  [PESO]                INT,
  [DT_CRIACAO]          DATETIME,
CONSTRAINT [PK_COMPONENTE] PRIMARY KEY ([ID_COMPONENTE])
)
GO

CREATE TABLE [SRC].[ELEMENTO] (
  [ID_ELEMENTO]       INT NOT NULL IDENTITY(1,1),
  [ID_USER]           INT NOT NULL,
  [ID_STATUS]         INT NOT NULL,
  [TITULO]            VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]        VARCHAR(20),
  [PESO]              INT,
  [DT_CRIACAO]        DATETIME,
  [DESCRICAO]         NVARCHAR(4000),
CONSTRAINT [PK_ELEMENTO] PRIMARY KEY ([ID_ELEMENTO])
)
GO

CREATE TABLE [SRC].[ACTION_STATUS] (
  [ID_ACTION_STATUS]            INT NOT NULL IDENTITY(1,1),
  [ID_STATUS_ORIGIN]            INT NOT NULL,
  [ID_STATUS_DESTINATION]       INT NOT NULL,
  [ID_ACTIONS]                  INT NOT NULL,
CONSTRAINT [PK_ACTION_STATUS] PRIMARY KEY ([ID_ACTION_STATUS]),
CONSTRAINT [UK_ACTION_STATUS] UNIQUE ([ID_ACTIONS],[ID_STATUS_DESTINATION],[ID_STATUS_ORIGIN])
)
GO

CREATE TABLE [SRC].[ITEM] (
  [ID_ITEM]           INT NOT NULL IDENTITY(1,1),
  [ID_ELEMENTO]       INT NOT NULL,
  [ID_USER]           INT NOT NULL,
  [ID_STATUS]         INT NOT NULL,
  [TITULO]            VARCHAR(255) NOT NULL,
  [TIPO_MEDIA]        VARCHAR(20),
  [PESO]              INT,
  [DT_CRIACAO]        DATETIME,
  [DESCRICAO]         NVARCHAR(4000),
  [AVALIACAO]         NVARCHAR(4000),
CONSTRAINT [PK_ITEM] PRIMARY KEY ([ID_ITEM])
)
GO

CREATE TABLE [SRC].[NOTA] (
  [ID_NOTA]            INT NOT NULL IDENTITY(1,1),
  [ID_USER]            INT NOT NULL,
  [ID_TIPO_NOTA]       INT,
  [ID_ELEMENTO]        INT,
  [NOTA]               FLOAT(3),
  [DT_CRIACAO]         DATETIME,
CONSTRAINT [PK_NOTA] PRIMARY KEY ([ID_NOTA])
)
GO

CREATE TABLE [SRC].[USER] (
  [ID_USER]        INT NOT NULL IDENTITY(1,1),
  [USERNAME]       VARCHAR(255) NOT NULL,
  [NAME]           VARCHAR(255) NOT NULL,
  [PASSWORD]       VARCHAR(255) NOT NULL,
  [EMAIL]          VARCHAR(255) NOT NULL,
  [MOBILE]         VARCHAR(30) NOT NULL,
  [ID_ROLE]        INT NOT NULL,
CONSTRAINT [PK_USER] PRIMARY KEY ([ID_USER]),
/*CAMPO USERNAME ?NICO.*/
CONSTRAINT [UK_USER] UNIQUE ([USERNAME])
)
GO

CREATE TABLE [SRC].[FEATURE] (
  [ID_FEATURE]       INT NOT NULL IDENTITY(1,1),
  [NAME]             VARCHAR(255) NOT NULL,
  [CODE]             VARCHAR(255) NOT NULL,
CONSTRAINT [PK_FEATURE] PRIMARY KEY ([ID_FEATURE])
)
GO

CREATE TABLE [SRC].[ROLE] (
  [ID_ROLE]       INT NOT NULL IDENTITY(1,1),
  [NAME]          VARCHAR(255) NOT NULL,
CONSTRAINT [PK_ROLE] PRIMARY KEY ([ID_ROLE])
)
GO

CREATE TABLE [SRC].[STATUS] (
  [ID_STATUS]        INT NOT NULL IDENTITY(1,1),
  [NAME]             VARCHAR(255) NOT NULL,
  [STEREOTYPE]       VARCHAR(255) NOT NULL,
CONSTRAINT [PK_STATUS] PRIMARY KEY ([ID_STATUS])
)
GO

CREATE TABLE [SRC].[ACTIONS] (
  [ID_ACTIONS]                  INT NOT NULL IDENTITY(1,1),
  [ID_ORIGIN_STATUS]            INT NOT NULL,
  [ID_DESTINATION_STATUS]       INT NOT NULL,
  [ID_WORKFLOW]                 INT NOT NULL,
  [NAME]                        VARCHAR(255) NOT NULL,
  [OTHER_THAN]                  BIT,
CONSTRAINT [PK_ACTIONS] PRIMARY KEY ([ID_ACTIONS])
)
GO

CREATE TABLE [SRC].[WORKFLOW] (
  [ID_WORKFLOW]       INT NOT NULL IDENTITY(1,1),
  [NAME]              VARCHAR(255) NOT NULL,
  [ENTITY_TYPE]       VARCHAR(50),
  [DT_START_AT]       DATE,
  [DT_END_AT]         DATE,
CONSTRAINT [PK_WORKFLOW] PRIMARY KEY ([ID_WORKFLOW])
)
GO

CREATE TABLE [SRC].[FEATURES_ACTIVITIES] (
  [ID_FEATURES_ACTIVITIES]       INT NOT NULL IDENTITY(1,1),
  [ID_ACTIVITY]                  INT NOT NULL,
  [ID_FEATURE]                   INT NOT NULL,
CONSTRAINT [PK_FEATURES_ACTIVITIES] PRIMARY KEY ([ID_FEATURES_ACTIVITIES]),
CONSTRAINT [UK_FEATURES_ACTIVITIES] UNIQUE ([ID_FEATURE],[ID_ACTIVITY])
)
GO

CREATE TABLE [SRC].[FEATURE_ROLE] (
  [ID_FEATURE_ROLE]       INT NOT NULL IDENTITY(1,1),
  [ID_FEATURE]            INT NOT NULL,
  [ID_ROLE]               INT NOT NULL,
CONSTRAINT [PK_FEATURE_ROLE] PRIMARY KEY ([ID_FEATURE_ROLE]),
/*CAMPOS UNIQUES*/
CONSTRAINT [UK_FEATURE_ROLE] UNIQUE ([ID_FEATURE],[ID_ROLE])
)
GO

CREATE TABLE [SRC].[ACTIVITY] (
  [ID_ACTIVITY]                   INT NOT NULL IDENTITY(1,1),
  [ID_EXPIRATION_ACTION]          INT,
  [ID_EXPIRATION_TIME_DAYS]       INT,
  [DT_START_AT]                   DATE,
  [DT_END_AT]                     DATE,
  [ID_ACTIONS]                    INT NOT NULL,
  [ID_WORKFLOW]                   INT NOT NULL,
CONSTRAINT [PK_ACTIVITY] PRIMARY KEY ([ID_ACTIVITY])
)
GO

CREATE TABLE [SRC].[ACTIVITIES_ROLES] (
  [ID_ACTIVITIES_ROLES]       INT NOT NULL IDENTITY(1,1),
  [ID_ROLE]                   INT NOT NULL,
  [ID_ACTIVITY]               INT NOT NULL,
CONSTRAINT [PK_ACTIVITIES_ROLES] PRIMARY KEY ([ID_ACTIVITIES_ROLES]),
CONSTRAINT [UK_ACTIVITIES_ROLES] UNIQUE ([ID_ROLE])
)
GO

/*============================================================================*/
/*                                 INDEXES                                    */
/*============================================================================*/
CREATE  INDEX [IX_ID_CARTEIRA] ON [SRC].[CARTEIRA]([ID_CARTEIRA] ASC)
GO

CREATE  INDEX [IX_ID_EQUIPE] ON [SRC].[EQUIPE]([ID_EQUIPE] ASC)
GO

CREATE  INDEX [IX_ID_PLANO] ON [SRC].[PLANO]([ID_PLANO] ASC)
GO

CREATE  INDEX [IX_ID_ENTIDADE] ON [SRC].[ENTIDADE]([ID_ENTIDADE] ASC)
GO

CREATE UNIQUE INDEX [IX_ID_CICLO] ON [SRC].[CICLO]([ID_CICLO] ASC)
GO

CREATE UNIQUE INDEX [IX_ID_MATRIZ] ON [SRC].[MATRIZ]([ID_MATRIZ] ASC)
GO

CREATE  INDEX [IX_ID_COMPONENTE] ON [SRC].[COMPONENTE]([ID_COMPONENTE] ASC)
GO

CREATE  INDEX [IX_ID_ELEMENTO] ON [SRC].[ELEMENTO]([ID_ELEMENTO] ASC)
GO

CREATE  INDEX [IX_ID_ACTION_STATUS] ON [SRC].[ACTION_STATUS]([ID_ACTION_STATUS] ASC)
GO
CREATE  INDEX [IX_ORIGIN_DESTINATION_STATUS] ON [SRC].[ACTION_STATUS]([ID_STATUS_ORIGIN],[ID_STATUS_DESTINATION],[ID_ACTIONS] ASC)
GO

CREATE  INDEX [IX_ID_ITEM] ON [SRC].[ITEM]([ID_ITEM] ASC)
GO

CREATE  INDEX [IX_ID_NOTA] ON [SRC].[NOTA]([ID_NOTA] ASC)
GO

CREATE  INDEX [IX_ID_USER] ON [SRC].[USER]([ID_USER] ASC)
GO

CREATE  INDEX [IX_ID_FEATURE] ON [SRC].[FEATURE]([ID_FEATURE] ASC)
GO
CREATE  INDEX [IX_NAME] ON [SRC].[FEATURE]([NAME],[CODE] ASC)
GO

/*============================================================================*/
/*                               FOREIGN KEYS                                 */
/*============================================================================*/
ALTER TABLE [SRC].[CARTEIRA]
    ADD CONSTRAINT [FK_ID_USER_CARTEIRA]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO

ALTER TABLE [SRC].[CARTEIRA]
    ADD CONSTRAINT [FK_ID_STATUS_CARTEIRA]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO


ALTER TABLE [SRC].[EQUIPE]
    ADD CONSTRAINT [FK_ID_USER_EQUIPE]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO

ALTER TABLE [SRC].[EQUIPE]
    ADD CONSTRAINT [FK_ID_STATUS_EQUIPE]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO


ALTER TABLE [SRC].[PLANO]
    ADD CONSTRAINT [FK_ID_USER_PLANO]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO

ALTER TABLE [SRC].[PLANO]
    ADD CONSTRAINT [FK_ID_STATUS_PLANO]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO


ALTER TABLE [SRC].[ENTIDADE]
    ADD CONSTRAINT [FK_ID_STATUS_ENTIDADE]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[ENTIDADE]
    ADD CONSTRAINT [FK_ID_USER_ENTIDADE]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO


ALTER TABLE [SRC].[CICLO]
    ADD CONSTRAINT [FK_ID_STATUS_CICLO]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[CICLO]
    ADD CONSTRAINT [FK_FK_ID_USER_CICLO]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO


ALTER TABLE [SRC].[MATRIZ]
    ADD CONSTRAINT [FK_ID_STATUS_MATRIZ]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[MATRIZ]
    ADD CONSTRAINT [FK_ID_USER_MATRIZ]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO


ALTER TABLE [SRC].[COMPONENTE]
    ADD CONSTRAINT [FK_STATUS]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[COMPONENTE]
    ADD CONSTRAINT [FK_ID_USERS_COMPONENTE]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO


ALTER TABLE [SRC].[ELEMENTO]
    ADD CONSTRAINT [FK_ID_USER_ELEMENTO]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO

ALTER TABLE [SRC].[ELEMENTO]
    ADD CONSTRAINT [FK_ID_STATUS_ELEMENTO]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO


ALTER TABLE [SRC].[ACTION_STATUS]
    ADD CONSTRAINT [FK_ID_STATUS_ORIGIN_ACTION_STATUS]
        FOREIGN KEY ([ID_STATUS_ORIGIN])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[ACTION_STATUS]
    ADD CONSTRAINT [FK_ID_STATUS_DESTINATION_ACTION_STATUS]
        FOREIGN KEY ([ID_STATUS_DESTINATION])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[ACTION_STATUS]
    ADD CONSTRAINT [FK_ID_ACTIONS_ACTION_STATUSA]
        FOREIGN KEY ([ID_ACTIONS])
            REFERENCES [SRC].[ACTIONS] ([ID_ACTIONS])
 GO


ALTER TABLE [SRC].[ITEM]
    ADD CONSTRAINT [FK_ID_STATUS_ITEM]
        FOREIGN KEY ([ID_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[ITEM]
    ADD CONSTRAINT [FK_ID_USER_ITEM]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO

ALTER TABLE [SRC].[ITEM]
    ADD CONSTRAINT [FK_ID_COMPONENTE]
        FOREIGN KEY ([ID_ELEMENTO])
            REFERENCES [SRC].[ELEMENTO] ([ID_ELEMENTO])
 GO


ALTER TABLE [SRC].[NOTA]
    ADD CONSTRAINT [FK_ID_USER_NOTA]
        FOREIGN KEY ([ID_USER])
            REFERENCES [SRC].[USER] ([ID_USER])
 GO


ALTER TABLE [SRC].[USER]
    ADD CONSTRAINT [FK_ID_ROLE_USER]
        FOREIGN KEY ([ID_ROLE])
            REFERENCES [SRC].[ROLE] ([ID_ROLE])
 GO


ALTER TABLE [SRC].[ACTIONS]
    ADD CONSTRAINT [FK_ID_STATUS_ACTIONS]
        FOREIGN KEY ([ID_DESTINATION_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[ACTIONS]
    ADD CONSTRAINT [FK_ID_ORIGIN_STATUS_ACTION_STATUS]
        FOREIGN KEY ([ID_ORIGIN_STATUS])
            REFERENCES [SRC].[STATUS] ([ID_STATUS])
 GO

ALTER TABLE [SRC].[ACTIONS]
    ADD CONSTRAINT [FK_ID_WORKFLOW]
        FOREIGN KEY ([ID_WORKFLOW])
            REFERENCES [SRC].[WORKFLOW] ([ID_WORKFLOW])
 GO


ALTER TABLE [SRC].[FEATURES_ACTIVITIES]
    ADD CONSTRAINT [FK_ID_ACTIVITY_FEATURES_ACTIVITIES]
        FOREIGN KEY ([ID_ACTIVITY])
            REFERENCES [SRC].[ACTIVITY] ([ID_ACTIVITY])
 GO

ALTER TABLE [SRC].[FEATURES_ACTIVITIES]
    ADD CONSTRAINT [FK_ID_FEATURE_FEAUTRES_ACTIVITIES]
        FOREIGN KEY ([ID_FEATURE])
            REFERENCES [SRC].[FEATURE] ([ID_FEATURE])
 GO


ALTER TABLE [SRC].[FEATURE_ROLE]
    ADD CONSTRAINT [FK_ID_FEATURE]
        FOREIGN KEY ([ID_FEATURE])
            REFERENCES [SRC].[FEATURE] ([ID_FEATURE])
 GO

ALTER TABLE [SRC].[FEATURE_ROLE]
    ADD CONSTRAINT [FK_ID_ROLE]
        FOREIGN KEY ([ID_ROLE])
            REFERENCES [SRC].[ROLE] ([ID_ROLE])
 GO


ALTER TABLE [SRC].[ACTIVITY]
    ADD CONSTRAINT [FK_ID_ACTIONS_ACTIVITY]
        FOREIGN KEY ([ID_ACTIONS])
            REFERENCES [SRC].[ACTIONS] ([ID_ACTIONS])
 GO

ALTER TABLE [SRC].[ACTIVITY]
    ADD CONSTRAINT [FK_ID_WORKFLOW_ACTIVITY]
        FOREIGN KEY ([ID_WORKFLOW])
            REFERENCES [SRC].[WORKFLOW] ([ID_WORKFLOW])
 GO


ALTER TABLE [SRC].[ACTIVITIES_ROLES]
    ADD CONSTRAINT [FK_ID_ROLE_ACTIVITIES_ROLES]
        FOREIGN KEY ([ID_ROLE])
            REFERENCES [SRC].[ROLE] ([ID_ROLE])
 GO

ALTER TABLE [SRC].[ACTIVITIES_ROLES]
    ADD CONSTRAINT [FK_ID_ACTIVITY_ACTIVITIES_ROLES]
        FOREIGN KEY ([ID_ACTIVITY])
            REFERENCES [SRC].[ACTIVITY] ([ID_ACTIVITY])
 GO


