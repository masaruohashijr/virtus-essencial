USE [SRC]
GO
/****** Object:  Schema [SRC]    Script Date: 27/09/2020 20:20:20 ******/
IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = N'SRC')
EXEC sys.sp_executesql N'CREATE SCHEMA [SRC]'

GO
/****** Object:  Table [SRC].[ACTION_STATUS]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[ACTION_STATUS]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[ACTION_STATUS](
	[ID_ACTION_STATUS] [int] IDENTITY(1,1) NOT NULL,
	[ID_STATUS_ORIGIN] [int] NOT NULL,
	[ID_STATUS_DESTINATION] [int] NOT NULL,
	[ID_ACTIONS] [int] NOT NULL,
 CONSTRAINT [PK_ACTION_STATUS] PRIMARY KEY CLUSTERED 
(
	[ID_ACTION_STATUS] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_ACTION_STATUS] UNIQUE NONCLUSTERED 
(
	[ID_ACTIONS] ASC,
	[ID_STATUS_DESTINATION] ASC,
	[ID_STATUS_ORIGIN] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [SRC].[ACTIONS]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[ACTIONS]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[ACTIONS](
	[ID_ACTIONS] [int] IDENTITY(1,1) NOT NULL,
	[ID_ORIGIN_STATUS] [int] NOT NULL,
	[ID_DESTINATION_STATUS] [int] NOT NULL,
	[ID_WORKFLOW] [int] NOT NULL,
	[NAME] [varchar](255) NOT NULL,
	[OTHER_THAN] [bit] NULL,
 CONSTRAINT [PK_ACTIONS] PRIMARY KEY CLUSTERED 
(
	[ID_ACTIONS] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[ACTIVITIES_ROLES]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[ACTIVITIES_ROLES]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[ACTIVITIES_ROLES](
	[ID_ACTIVITIES_ROLES] [int] IDENTITY(1,1) NOT NULL,
	[ID_ROLE] [int] NOT NULL,
	[ID_ACTIVITY] [int] NOT NULL,
 CONSTRAINT [PK_ACTIVITIES_ROLES] PRIMARY KEY CLUSTERED 
(
	[ID_ACTIVITIES_ROLES] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_ACTIVITIES_ROLES] UNIQUE NONCLUSTERED 
(
	[ID_ROLE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [SRC].[ACTIVITY]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[ACTIVITY]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[ACTIVITY](
	[ID_ACTIVITY] [int] IDENTITY(1,1) NOT NULL,
	[ID_EXPIRATION_ACTION] [int] NULL,
	[ID_EXPIRATION_TIME_DAYS] [int] NULL,
	[DT_START_AT] [date] NULL,
	[DT_END_AT] [date] NULL,
	[ID_ACTIONS] [int] NOT NULL,
	[ID_WORKFLOW] [int] NOT NULL,
 CONSTRAINT [PK_ACTIVITY] PRIMARY KEY CLUSTERED 
(
	[ID_ACTIVITY] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [SRC].[CARTEIRA]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[CARTEIRA]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[CARTEIRA](
	[ID_CARTEIRA] [int] IDENTITY(1,1) NOT NULL,
	[ID_USER] [int] NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
 CONSTRAINT [PK_CARTEIRA] PRIMARY KEY CLUSTERED 
(
	[ID_CARTEIRA] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[CICLO]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[CICLO]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[CICLO](
	[ID_CICLO] [int] IDENTITY(1,1) NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[ID_USER] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
 CONSTRAINT [PK_CICLO] PRIMARY KEY CLUSTERED 
(
	[ID_CICLO] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[COMPONENTE]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[COMPONENTE]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[COMPONENTE](
	[ID_COMPONENTE] [int] IDENTITY(1,1) NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[ID_USER] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
 CONSTRAINT [PK_COMPONENTE] PRIMARY KEY CLUSTERED 
(
	[ID_COMPONENTE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[ELEMENTO]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[ELEMENTO]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[ELEMENTO](
	[ID_ELEMENTO] [int] IDENTITY(1,1) NOT NULL,
	[ID_USER] [int] NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
	[DESCRICAO] [nvarchar](4000) NULL,
 CONSTRAINT [PK_ELEMENTO] PRIMARY KEY CLUSTERED 
(
	[ID_ELEMENTO] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[ENTIDADE]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[ENTIDADE]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[ENTIDADE](
	[ID_ENTIDADE] [int] IDENTITY(1,1) NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[ID_USER] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
 CONSTRAINT [PK_ENTIDADE] PRIMARY KEY CLUSTERED 
(
	[ID_ENTIDADE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[EQUIPE]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[EQUIPE]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[EQUIPE](
	[ID_EQUIPE] [int] IDENTITY(1,1) NOT NULL,
	[ID_USER] [int] NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
 CONSTRAINT [PK_EQUIPE] PRIMARY KEY CLUSTERED 
(
	[ID_EQUIPE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[FEATURE]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[FEATURE]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[FEATURE](
	[ID_FEATURE] [int] IDENTITY(1,1) NOT NULL,
	[NAME] [varchar](255) NOT NULL,
	[CODE] [varchar](255) NOT NULL,
 CONSTRAINT [PK_FEATURE] PRIMARY KEY CLUSTERED 
(
	[ID_FEATURE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[FEATURE_ROLE]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[FEATURE_ROLE]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[FEATURE_ROLE](
	[ID_FEATURE_ROLE] [int] IDENTITY(1,1) NOT NULL,
	[ID_FEATURE] [int] NOT NULL,
	[ID_ROLE] [int] NOT NULL,
 CONSTRAINT [PK_FEATURE_ROLE] PRIMARY KEY CLUSTERED 
(
	[ID_FEATURE_ROLE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_FEATURE_ROLE] UNIQUE NONCLUSTERED 
(
	[ID_FEATURE] ASC,
	[ID_ROLE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [SRC].[FEATURES_ACTIVITIES]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[FEATURES_ACTIVITIES]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[FEATURES_ACTIVITIES](
	[ID_FEATURES_ACTIVITIES] [int] IDENTITY(1,1) NOT NULL,
	[ID_ACTIVITY] [int] NOT NULL,
	[ID_FEATURE] [int] NOT NULL,
 CONSTRAINT [PK_FEATURES_ACTIVITIES] PRIMARY KEY CLUSTERED 
(
	[ID_FEATURES_ACTIVITIES] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_FEATURES_ACTIVITIES] UNIQUE NONCLUSTERED 
(
	[ID_FEATURE] ASC,
	[ID_ACTIVITY] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [SRC].[ITEM]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[ITEM]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[ITEM](
	[ID_ITEM] [int] IDENTITY(1,1) NOT NULL,
	[ID_ELEMENTO] [int] NOT NULL,
	[ID_USER] [int] NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
	[DESCRICAO] [nvarchar](4000) NULL,
	[AVALIACAO] [nvarchar](4000) NULL,
 CONSTRAINT [PK_ITEM] PRIMARY KEY CLUSTERED 
(
	[ID_ITEM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[MATRIZ]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[MATRIZ]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[MATRIZ](
	[ID_MATRIZ] [int] IDENTITY(1,1) NOT NULL,
	[ID_USER] [int] NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
 CONSTRAINT [PK_MATRIZ] PRIMARY KEY CLUSTERED 
(
	[ID_MATRIZ] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[NOTA]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[NOTA]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[NOTA](
	[ID_NOTA] [int] IDENTITY(1,1) NOT NULL,
	[ID_USER] [int] NOT NULL,
	[ID_TIPO_NOTA] [int] NULL,
	[ID_ELEMENTO] [int] NULL,
	[NOTA] [real] NULL,
	[DT_CRIACAO] [datetime] NULL,
 CONSTRAINT [PK_NOTA] PRIMARY KEY CLUSTERED 
(
	[ID_NOTA] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [SRC].[PLANO]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[PLANO]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[PLANO](
	[ID_PLANO] [int] IDENTITY(1,1) NOT NULL,
	[ID_USER] [int] NOT NULL,
	[ID_STATUS] [int] NOT NULL,
	[TITULO] [varchar](255) NOT NULL,
	[TIPO_MEDIA] [varchar](20) NULL,
	[PESO] [int] NULL,
	[DT_CRIACAO] [datetime] NULL,
 CONSTRAINT [PK_PLANO] PRIMARY KEY CLUSTERED 
(
	[ID_PLANO] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[ROLE]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[ROLE]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[ROLE](
	[ID_ROLE] [int] IDENTITY(1,1) NOT NULL,
	[NAME] [varchar](255) NOT NULL,
 CONSTRAINT [PK_ROLE] PRIMARY KEY CLUSTERED 
(
	[ID_ROLE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[STATUS]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[STATUS]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[STATUS](
	[ID_STATUS] [int] IDENTITY(1,1) NOT NULL,
	[NAME] [varchar](255) NOT NULL,
	[STEREOTYPE] [varchar](255) NOT NULL,
 CONSTRAINT [PK_STATUS] PRIMARY KEY CLUSTERED 
(
	[ID_STATUS] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[USER]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[USER]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[USER](
	[ID_USER] [int] IDENTITY(1,1) NOT NULL,
	[USERNAME] [varchar](255) NOT NULL,
	[NAME] [varchar](255) NOT NULL,
	[PASSWORD] [varchar](255) NOT NULL,
	[EMAIL] [varchar](255) NOT NULL,
	[MOBILE] [varchar](30) NOT NULL,
	[ID_ROLE] [int] NOT NULL,
 CONSTRAINT [PK_USER] PRIMARY KEY CLUSTERED 
(
	[ID_USER] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_USER] UNIQUE NONCLUSTERED 
(
	[USERNAME] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [SRC].[WORKFLOW]    Script Date: 27/09/2020 20:20:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[SRC].[WORKFLOW]') AND type in (N'U'))
BEGIN
CREATE TABLE [SRC].[WORKFLOW](
	[ID_WORKFLOW] [int] IDENTITY(1,1) NOT NULL,
	[NAME] [varchar](255) NOT NULL,
	[ENTITY_TYPE] [varchar](50) NULL,
	[DT_START_AT] [date] NULL,
	[DT_END_AT] [date] NULL,
 CONSTRAINT [PK_WORKFLOW] PRIMARY KEY CLUSTERED 
(
	[ID_WORKFLOW] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ACTIONS_ACTION_STATUSA]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTION_STATUS]'))
ALTER TABLE [SRC].[ACTION_STATUS]  WITH CHECK ADD  CONSTRAINT [FK_ID_ACTIONS_ACTION_STATUSA] FOREIGN KEY([ID_ACTIONS])
REFERENCES [SRC].[ACTIONS] ([ID_ACTIONS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ACTIONS_ACTION_STATUSA]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTION_STATUS]'))
ALTER TABLE [SRC].[ACTION_STATUS] CHECK CONSTRAINT [FK_ID_ACTIONS_ACTION_STATUSA]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_DESTINATION_ACTION_STATUS]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTION_STATUS]'))
ALTER TABLE [SRC].[ACTION_STATUS]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_DESTINATION_ACTION_STATUS] FOREIGN KEY([ID_STATUS_DESTINATION])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_DESTINATION_ACTION_STATUS]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTION_STATUS]'))
ALTER TABLE [SRC].[ACTION_STATUS] CHECK CONSTRAINT [FK_ID_STATUS_DESTINATION_ACTION_STATUS]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ORIGIN_ACTION_STATUS]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTION_STATUS]'))
ALTER TABLE [SRC].[ACTION_STATUS]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_ORIGIN_ACTION_STATUS] FOREIGN KEY([ID_STATUS_ORIGIN])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ORIGIN_ACTION_STATUS]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTION_STATUS]'))
ALTER TABLE [SRC].[ACTION_STATUS] CHECK CONSTRAINT [FK_ID_STATUS_ORIGIN_ACTION_STATUS]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ORIGIN_STATUS_ACTION_STATUS]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIONS]'))
ALTER TABLE [SRC].[ACTIONS]  WITH CHECK ADD  CONSTRAINT [FK_ID_ORIGIN_STATUS_ACTION_STATUS] FOREIGN KEY([ID_ORIGIN_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ORIGIN_STATUS_ACTION_STATUS]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIONS]'))
ALTER TABLE [SRC].[ACTIONS] CHECK CONSTRAINT [FK_ID_ORIGIN_STATUS_ACTION_STATUS]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ACTIONS]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIONS]'))
ALTER TABLE [SRC].[ACTIONS]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_ACTIONS] FOREIGN KEY([ID_DESTINATION_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ACTIONS]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIONS]'))
ALTER TABLE [SRC].[ACTIONS] CHECK CONSTRAINT [FK_ID_STATUS_ACTIONS]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_WORKFLOW]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIONS]'))
ALTER TABLE [SRC].[ACTIONS]  WITH CHECK ADD  CONSTRAINT [FK_ID_WORKFLOW] FOREIGN KEY([ID_WORKFLOW])
REFERENCES [SRC].[WORKFLOW] ([ID_WORKFLOW])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_WORKFLOW]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIONS]'))
ALTER TABLE [SRC].[ACTIONS] CHECK CONSTRAINT [FK_ID_WORKFLOW]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ACTIVITY_ACTIVITIES_ROLES]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIVITIES_ROLES]'))
ALTER TABLE [SRC].[ACTIVITIES_ROLES]  WITH CHECK ADD  CONSTRAINT [FK_ID_ACTIVITY_ACTIVITIES_ROLES] FOREIGN KEY([ID_ACTIVITY])
REFERENCES [SRC].[ACTIVITY] ([ID_ACTIVITY])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ACTIVITY_ACTIVITIES_ROLES]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIVITIES_ROLES]'))
ALTER TABLE [SRC].[ACTIVITIES_ROLES] CHECK CONSTRAINT [FK_ID_ACTIVITY_ACTIVITIES_ROLES]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ROLE_ACTIVITIES_ROLES]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIVITIES_ROLES]'))
ALTER TABLE [SRC].[ACTIVITIES_ROLES]  WITH CHECK ADD  CONSTRAINT [FK_ID_ROLE_ACTIVITIES_ROLES] FOREIGN KEY([ID_ROLE])
REFERENCES [SRC].[ROLE] ([ID_ROLE])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ROLE_ACTIVITIES_ROLES]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIVITIES_ROLES]'))
ALTER TABLE [SRC].[ACTIVITIES_ROLES] CHECK CONSTRAINT [FK_ID_ROLE_ACTIVITIES_ROLES]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ACTIONS_ACTIVITY]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIVITY]'))
ALTER TABLE [SRC].[ACTIVITY]  WITH CHECK ADD  CONSTRAINT [FK_ID_ACTIONS_ACTIVITY] FOREIGN KEY([ID_ACTIONS])
REFERENCES [SRC].[ACTIONS] ([ID_ACTIONS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ACTIONS_ACTIVITY]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIVITY]'))
ALTER TABLE [SRC].[ACTIVITY] CHECK CONSTRAINT [FK_ID_ACTIONS_ACTIVITY]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_WORKFLOW_ACTIVITY]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIVITY]'))
ALTER TABLE [SRC].[ACTIVITY]  WITH CHECK ADD  CONSTRAINT [FK_ID_WORKFLOW_ACTIVITY] FOREIGN KEY([ID_WORKFLOW])
REFERENCES [SRC].[WORKFLOW] ([ID_WORKFLOW])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_WORKFLOW_ACTIVITY]') AND parent_object_id = OBJECT_ID(N'[SRC].[ACTIVITY]'))
ALTER TABLE [SRC].[ACTIVITY] CHECK CONSTRAINT [FK_ID_WORKFLOW_ACTIVITY]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_CARTEIRA]') AND parent_object_id = OBJECT_ID(N'[SRC].[CARTEIRA]'))
ALTER TABLE [SRC].[CARTEIRA]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_CARTEIRA] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_CARTEIRA]') AND parent_object_id = OBJECT_ID(N'[SRC].[CARTEIRA]'))
ALTER TABLE [SRC].[CARTEIRA] CHECK CONSTRAINT [FK_ID_STATUS_CARTEIRA]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_CARTEIRA]') AND parent_object_id = OBJECT_ID(N'[SRC].[CARTEIRA]'))
ALTER TABLE [SRC].[CARTEIRA]  WITH CHECK ADD  CONSTRAINT [FK_ID_USER_CARTEIRA] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_CARTEIRA]') AND parent_object_id = OBJECT_ID(N'[SRC].[CARTEIRA]'))
ALTER TABLE [SRC].[CARTEIRA] CHECK CONSTRAINT [FK_ID_USER_CARTEIRA]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_FK_ID_USER_CICLO]') AND parent_object_id = OBJECT_ID(N'[SRC].[CICLO]'))
ALTER TABLE [SRC].[CICLO]  WITH CHECK ADD  CONSTRAINT [FK_FK_ID_USER_CICLO] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_FK_ID_USER_CICLO]') AND parent_object_id = OBJECT_ID(N'[SRC].[CICLO]'))
ALTER TABLE [SRC].[CICLO] CHECK CONSTRAINT [FK_FK_ID_USER_CICLO]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_CICLO]') AND parent_object_id = OBJECT_ID(N'[SRC].[CICLO]'))
ALTER TABLE [SRC].[CICLO]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_CICLO] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_CICLO]') AND parent_object_id = OBJECT_ID(N'[SRC].[CICLO]'))
ALTER TABLE [SRC].[CICLO] CHECK CONSTRAINT [FK_ID_STATUS_CICLO]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USERS_COMPONENTE]') AND parent_object_id = OBJECT_ID(N'[SRC].[COMPONENTE]'))
ALTER TABLE [SRC].[COMPONENTE]  WITH CHECK ADD  CONSTRAINT [FK_ID_USERS_COMPONENTE] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USERS_COMPONENTE]') AND parent_object_id = OBJECT_ID(N'[SRC].[COMPONENTE]'))
ALTER TABLE [SRC].[COMPONENTE] CHECK CONSTRAINT [FK_ID_USERS_COMPONENTE]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_STATUS]') AND parent_object_id = OBJECT_ID(N'[SRC].[COMPONENTE]'))
ALTER TABLE [SRC].[COMPONENTE]  WITH CHECK ADD  CONSTRAINT [FK_STATUS] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_STATUS]') AND parent_object_id = OBJECT_ID(N'[SRC].[COMPONENTE]'))
ALTER TABLE [SRC].[COMPONENTE] CHECK CONSTRAINT [FK_STATUS]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ELEMENTO]') AND parent_object_id = OBJECT_ID(N'[SRC].[ELEMENTO]'))
ALTER TABLE [SRC].[ELEMENTO]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_ELEMENTO] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ELEMENTO]') AND parent_object_id = OBJECT_ID(N'[SRC].[ELEMENTO]'))
ALTER TABLE [SRC].[ELEMENTO] CHECK CONSTRAINT [FK_ID_STATUS_ELEMENTO]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_ELEMENTO]') AND parent_object_id = OBJECT_ID(N'[SRC].[ELEMENTO]'))
ALTER TABLE [SRC].[ELEMENTO]  WITH CHECK ADD  CONSTRAINT [FK_ID_USER_ELEMENTO] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_ELEMENTO]') AND parent_object_id = OBJECT_ID(N'[SRC].[ELEMENTO]'))
ALTER TABLE [SRC].[ELEMENTO] CHECK CONSTRAINT [FK_ID_USER_ELEMENTO]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ENTIDADE]') AND parent_object_id = OBJECT_ID(N'[SRC].[ENTIDADE]'))
ALTER TABLE [SRC].[ENTIDADE]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_ENTIDADE] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ENTIDADE]') AND parent_object_id = OBJECT_ID(N'[SRC].[ENTIDADE]'))
ALTER TABLE [SRC].[ENTIDADE] CHECK CONSTRAINT [FK_ID_STATUS_ENTIDADE]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_ENTIDADE]') AND parent_object_id = OBJECT_ID(N'[SRC].[ENTIDADE]'))
ALTER TABLE [SRC].[ENTIDADE]  WITH CHECK ADD  CONSTRAINT [FK_ID_USER_ENTIDADE] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_ENTIDADE]') AND parent_object_id = OBJECT_ID(N'[SRC].[ENTIDADE]'))
ALTER TABLE [SRC].[ENTIDADE] CHECK CONSTRAINT [FK_ID_USER_ENTIDADE]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_EQUIPE]') AND parent_object_id = OBJECT_ID(N'[SRC].[EQUIPE]'))
ALTER TABLE [SRC].[EQUIPE]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_EQUIPE] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_EQUIPE]') AND parent_object_id = OBJECT_ID(N'[SRC].[EQUIPE]'))
ALTER TABLE [SRC].[EQUIPE] CHECK CONSTRAINT [FK_ID_STATUS_EQUIPE]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_EQUIPE]') AND parent_object_id = OBJECT_ID(N'[SRC].[EQUIPE]'))
ALTER TABLE [SRC].[EQUIPE]  WITH CHECK ADD  CONSTRAINT [FK_ID_USER_EQUIPE] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_EQUIPE]') AND parent_object_id = OBJECT_ID(N'[SRC].[EQUIPE]'))
ALTER TABLE [SRC].[EQUIPE] CHECK CONSTRAINT [FK_ID_USER_EQUIPE]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_FEATURE]') AND parent_object_id = OBJECT_ID(N'[SRC].[FEATURE_ROLE]'))
ALTER TABLE [SRC].[FEATURE_ROLE]  WITH CHECK ADD  CONSTRAINT [FK_ID_FEATURE] FOREIGN KEY([ID_FEATURE])
REFERENCES [SRC].[FEATURE] ([ID_FEATURE])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_FEATURE]') AND parent_object_id = OBJECT_ID(N'[SRC].[FEATURE_ROLE]'))
ALTER TABLE [SRC].[FEATURE_ROLE] CHECK CONSTRAINT [FK_ID_FEATURE]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ROLE]') AND parent_object_id = OBJECT_ID(N'[SRC].[FEATURE_ROLE]'))
ALTER TABLE [SRC].[FEATURE_ROLE]  WITH CHECK ADD  CONSTRAINT [FK_ID_ROLE] FOREIGN KEY([ID_ROLE])
REFERENCES [SRC].[ROLE] ([ID_ROLE])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ROLE]') AND parent_object_id = OBJECT_ID(N'[SRC].[FEATURE_ROLE]'))
ALTER TABLE [SRC].[FEATURE_ROLE] CHECK CONSTRAINT [FK_ID_ROLE]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ACTIVITY_FEATURES_ACTIVITIES]') AND parent_object_id = OBJECT_ID(N'[SRC].[FEATURES_ACTIVITIES]'))
ALTER TABLE [SRC].[FEATURES_ACTIVITIES]  WITH CHECK ADD  CONSTRAINT [FK_ID_ACTIVITY_FEATURES_ACTIVITIES] FOREIGN KEY([ID_ACTIVITY])
REFERENCES [SRC].[ACTIVITY] ([ID_ACTIVITY])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ACTIVITY_FEATURES_ACTIVITIES]') AND parent_object_id = OBJECT_ID(N'[SRC].[FEATURES_ACTIVITIES]'))
ALTER TABLE [SRC].[FEATURES_ACTIVITIES] CHECK CONSTRAINT [FK_ID_ACTIVITY_FEATURES_ACTIVITIES]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_FEATURE_FEAUTRES_ACTIVITIES]') AND parent_object_id = OBJECT_ID(N'[SRC].[FEATURES_ACTIVITIES]'))
ALTER TABLE [SRC].[FEATURES_ACTIVITIES]  WITH CHECK ADD  CONSTRAINT [FK_ID_FEATURE_FEAUTRES_ACTIVITIES] FOREIGN KEY([ID_FEATURE])
REFERENCES [SRC].[FEATURE] ([ID_FEATURE])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_FEATURE_FEAUTRES_ACTIVITIES]') AND parent_object_id = OBJECT_ID(N'[SRC].[FEATURES_ACTIVITIES]'))
ALTER TABLE [SRC].[FEATURES_ACTIVITIES] CHECK CONSTRAINT [FK_ID_FEATURE_FEAUTRES_ACTIVITIES]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_COMPONENTE]') AND parent_object_id = OBJECT_ID(N'[SRC].[ITEM]'))
ALTER TABLE [SRC].[ITEM]  WITH CHECK ADD  CONSTRAINT [FK_ID_COMPONENTE] FOREIGN KEY([ID_ELEMENTO])
REFERENCES [SRC].[ELEMENTO] ([ID_ELEMENTO])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_COMPONENTE]') AND parent_object_id = OBJECT_ID(N'[SRC].[ITEM]'))
ALTER TABLE [SRC].[ITEM] CHECK CONSTRAINT [FK_ID_COMPONENTE]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ITEM]') AND parent_object_id = OBJECT_ID(N'[SRC].[ITEM]'))
ALTER TABLE [SRC].[ITEM]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_ITEM] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_ITEM]') AND parent_object_id = OBJECT_ID(N'[SRC].[ITEM]'))
ALTER TABLE [SRC].[ITEM] CHECK CONSTRAINT [FK_ID_STATUS_ITEM]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_ITEM]') AND parent_object_id = OBJECT_ID(N'[SRC].[ITEM]'))
ALTER TABLE [SRC].[ITEM]  WITH CHECK ADD  CONSTRAINT [FK_ID_USER_ITEM] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_ITEM]') AND parent_object_id = OBJECT_ID(N'[SRC].[ITEM]'))
ALTER TABLE [SRC].[ITEM] CHECK CONSTRAINT [FK_ID_USER_ITEM]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_MATRIZ]') AND parent_object_id = OBJECT_ID(N'[SRC].[MATRIZ]'))
ALTER TABLE [SRC].[MATRIZ]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_MATRIZ] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_MATRIZ]') AND parent_object_id = OBJECT_ID(N'[SRC].[MATRIZ]'))
ALTER TABLE [SRC].[MATRIZ] CHECK CONSTRAINT [FK_ID_STATUS_MATRIZ]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_MATRIZ]') AND parent_object_id = OBJECT_ID(N'[SRC].[MATRIZ]'))
ALTER TABLE [SRC].[MATRIZ]  WITH CHECK ADD  CONSTRAINT [FK_ID_USER_MATRIZ] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_MATRIZ]') AND parent_object_id = OBJECT_ID(N'[SRC].[MATRIZ]'))
ALTER TABLE [SRC].[MATRIZ] CHECK CONSTRAINT [FK_ID_USER_MATRIZ]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_NOTA]') AND parent_object_id = OBJECT_ID(N'[SRC].[NOTA]'))
ALTER TABLE [SRC].[NOTA]  WITH CHECK ADD  CONSTRAINT [FK_ID_USER_NOTA] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_NOTA]') AND parent_object_id = OBJECT_ID(N'[SRC].[NOTA]'))
ALTER TABLE [SRC].[NOTA] CHECK CONSTRAINT [FK_ID_USER_NOTA]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_PLANO]') AND parent_object_id = OBJECT_ID(N'[SRC].[PLANO]'))
ALTER TABLE [SRC].[PLANO]  WITH CHECK ADD  CONSTRAINT [FK_ID_STATUS_PLANO] FOREIGN KEY([ID_STATUS])
REFERENCES [SRC].[STATUS] ([ID_STATUS])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_STATUS_PLANO]') AND parent_object_id = OBJECT_ID(N'[SRC].[PLANO]'))
ALTER TABLE [SRC].[PLANO] CHECK CONSTRAINT [FK_ID_STATUS_PLANO]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_PLANO]') AND parent_object_id = OBJECT_ID(N'[SRC].[PLANO]'))
ALTER TABLE [SRC].[PLANO]  WITH CHECK ADD  CONSTRAINT [FK_ID_USER_PLANO] FOREIGN KEY([ID_USER])
REFERENCES [SRC].[USER] ([ID_USER])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_USER_PLANO]') AND parent_object_id = OBJECT_ID(N'[SRC].[PLANO]'))
ALTER TABLE [SRC].[PLANO] CHECK CONSTRAINT [FK_ID_USER_PLANO]
GO
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ROLE_USER]') AND parent_object_id = OBJECT_ID(N'[SRC].[USER]'))
ALTER TABLE [SRC].[USER]  WITH CHECK ADD  CONSTRAINT [FK_ID_ROLE_USER] FOREIGN KEY([ID_ROLE])
REFERENCES [SRC].[ROLE] ([ID_ROLE])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[SRC].[FK_ID_ROLE_USER]') AND parent_object_id = OBJECT_ID(N'[SRC].[USER]'))
ALTER TABLE [SRC].[USER] CHECK CONSTRAINT [FK_ID_ROLE_USER]
GO
