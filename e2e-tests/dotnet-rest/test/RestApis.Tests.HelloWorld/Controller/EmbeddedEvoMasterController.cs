﻿using System.Collections.Generic;
using System.Diagnostics;
using System.Threading.Tasks;
using Controller;
using Controller.Api;
using Controller.Problem;

namespace RestApis.Tests.HelloWorld.Controller {

    public class EmbeddedEvoMasterController : EmbeddedSutController {

        private bool isSutRunning;
        private int sutPort;

        public static void Main (string[] args) {

            EmbeddedEvoMasterController embeddedEvoMasterController = new EmbeddedEvoMasterController ();

            InstrumentedSutStarter instrumentedSutStarter = new InstrumentedSutStarter (embeddedEvoMasterController);

            System.Console.WriteLine ("Driver is starting...\n");

            instrumentedSutStarter.Start ();
        }

        public override string GetDatabaseDriverName () => null;

        public override List<AuthenticationDto> GetInfoForAuthentication () => null;

        public override string GetPackagePrefixesToCover () => "RestApis.HelloWorld";

        //TODO: later on we should create sth specific for C#
        public override OutputFormat GetPreferredOutputFormat () => OutputFormat.JAVA_JUNIT_5;

        //TODO: check again
        public override IProblemInfo GetProblemInfo () =>
            GetSutPort () != 0 ? new RestProblem ("http://localhost:" + GetSutPort () + "/swagger/v1/swagger.json", null) : new RestProblem (null, null);

        public override bool IsSutRunning () => isSutRunning;

        public override void ResetStateOfSut () { }

        public override string StartSut () {

            //TODO: check this again
            int ephemeralPort = GetEphemeralTcpPort ();

            var task = Task.Run (() => {

                RestApis.HelloWorld.Program.Main (new string[] { ephemeralPort.ToString () });
            });

            WaitUntilSutIsRunning (ephemeralPort);

            sutPort = ephemeralPort;

            isSutRunning = true;

            return $"http://localhost:{ephemeralPort}";
        }

        public override void StopSut () {

            RestApis.HelloWorld.Program.Shutdown ();

            isSutRunning = false;
        }

        protected int GetSutPort () => sutPort;
    }
}