/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {ActiveServiceLogEntry} from '@app/classes/active-service-log-entry';

export interface AppState {
  isAuthorized: boolean;
  isInitialLoading: boolean;
  isLoginInProgress: boolean;
  activeLogsType?: string;
  isServiceLogsFileView: boolean;
  isServiceLogContextView: boolean;
  activeLog: ActiveServiceLogEntry | null;
  activeFilters: object;
}

export const initialState: AppState = {
  isAuthorized: false,
  isInitialLoading: false,
  isLoginInProgress: false,
  activeLogsType: 'serviceLogs',
  isServiceLogsFileView: false,
  isServiceLogContextView: false,
  activeLog: null,
  activeFilters: null
};
